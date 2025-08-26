package fr.openmc.core.features.tickets.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.tickets.PlayerStats;
import fr.openmc.core.features.tickets.TicketManager;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MachineBallsOpenMenu extends Menu {

    private BukkitTask animationTask;
    private boolean isAnimating = false;
    private int animationTick = 0;
    private final int maxAnimationTicks = 60;
    private final List<LootItem> lootItems;
    private final List<Integer> displaySlots = Arrays.asList(19, 20, 21, 22, 23, 24, 25);

    private int itemOffset = 0;
    private LootItem winningItem = null;
    private boolean finished = false;

    public MachineBallsOpenMenu(@NotNull Player owner) {
        super(owner);
        this.lootItems = initializeLootItems();
        startAnimation();
    }

    @Override
    public @NotNull String getName() {
        return "§6§lMachine à Boules";
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGER;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (animationTask != null && !animationTask.isCancelled()) {
            animationTask.cancel();
        }
        Player player = (Player) event.getPlayer();
        player.playSound(Sound.sound(Key.key("minecraft", "block.barrel.close"),
                Sound.Source.BLOCK, 1f, 1f));
    }

    private List<LootItem> generateWeightedPool() {
        List<LootItem> pool = new ArrayList<>();
        for (LootItem item : lootItems) {
            int count = Math.max(1, (int) (item.chance() * 2));
            for (int i = 0; i < count; i++) {
                pool.add(item);
            }
        }
        Collections.shuffle(pool);
        return pool;
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> items = fill(Material.GRAY_STAINED_GLASS_PANE);


        if (!isAnimating && !finished)
            startAnimation();

        if (finished && winningItem != null) {
            items.put(22, new ItemBuilder(this, winningItem.material(), meta -> {
                meta.displayName(Component.text("§6§l✦ ")
                        .append(Component.text(winningItem.displayName()))
                        .append(Component.text(" §6§l✦")));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("§e§lFÉLICITATIONS !"));
                lore.add(Component.text(" "));
                lore.addAll(winningItem.lore());
                meta.lore(lore);
            }));
            return items;
        }

        List<LootItem> weightedPool = generateWeightedPool();
        for (int i = 0; i < displaySlots.size(); i++) {
            int lootIndex = (itemOffset + i) % weightedPool.size();
            LootItem lootItem = weightedPool.get(lootIndex);

            items.put(displaySlots.get(i), new ItemBuilder(this, lootItem.material(), meta -> {
                meta.displayName(Component.text(lootItem.displayName()));
                meta.lore(lootItem.lore());
            }));
        }

        return items;
    }

    public void startAnimation() {
        if (isAnimating || finished) return;

        isAnimating = true;
        animationTick = 0;

        getOwner().playSound(Sound.sound(Key.key("minecraft", "block.note_block.pling"),
                Sound.Source.BLOCK, 1f, 1f));

        winningItem = selectRandomLoot();
        List<LootItem> weightedPool = generateWeightedPool();

        animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (animationTick >= maxAnimationTicks) {
                    finishAnimation();
                    cancel();
                    return;
                }

                itemOffset = (itemOffset + 1) % weightedPool.size();
                refreshAnimated(weightedPool);

                if (animationTick % 2 == 0) {
                    getOwner().playSound(Sound.sound(Key.key("minecraft", "ui.button.click"),
                            Sound.Source.BLOCK, 1f, 1f + (animationTick / (float) maxAnimationTicks)));
                }

                animationTick++;
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 4L);
    }

    private void finishAnimation() {
        winningItem = selectRandomLoot();
        finished = true;

        if (!TicketManager.useTicket(getOwner().getUniqueId())) {
            MessagesManager.sendMessage(getOwner(),
                    Component.text("§cVous n'avez pas assez de tickets !"),
                    Prefix.OPENMC, MessageType.ERROR, true);
            getOwner().closeInventory();
            return;
        }

        getOwner().playSound(Sound.sound(Key.key("minecraft", "entity.player.levelup"),
                Sound.Source.BLOCK, 1f, 1f));

        refresh();

        new BukkitRunnable() {
            @Override
            public void run() {
                giveReward(winningItem);
                getOwner().closeInventory();
                if (winningItem.chance() <= 10.0) {
                    getOwner().playSound(Sound.sound(Key.key("minecraft", "entity.firework_rocket.launch"),
                            Sound.Source.BLOCK, 1f, 1f));

                    getOwner().getWorld().spawn(getOwner().getLocation(), Firework.class, firework -> {
                        FireworkMeta meta = firework.getFireworkMeta();
                        meta.addEffect(FireworkEffect.builder()
                                .withColor(Color.ORANGE)
                                .with(FireworkEffect.Type.BALL_LARGE)
                                .withFlicker()
                                .build());
                        meta.setPower(2);
                        firework.setFireworkMeta(meta);
                        firework.detonate();
                    });
                    MessagesManager.broadcastMessage(
                            Component.text("§6§l✦ §e§lFÉLICITATIONS §r§eà ")
                                    .append(Component.text(getOwner().getName()))
                                    .append(Component.text(" §equi vient de gagner "))
                                    .append(Component.text(winningItem.displayName()))
                                    .append(Component.text(" §eà la machine à boules ! §6§l✦")),
                            Prefix.OPENMC, MessageType.INFO);
                }
            }
        }.runTaskLater(OMCPlugin.getInstance(), 60L);
    }

    private LootItem selectRandomLoot() {
        double random = ThreadLocalRandom.current().nextDouble() * 100;
        double cumulative = 0;
        for (LootItem item : lootItems) {
            cumulative += item.chance();
            if (random <= cumulative) return item;
        }
        return lootItems.getLast();
    }

    private void giveReward(LootItem wonItem) {
        PlayerStats ps = TicketManager.getPlayerStats(getOwner().getUniqueId());
        if (ps == null) return;

        String itemKey = wonItem.displayName();
        int alreadyWon = ps.getMaxItemsGiven().getOrDefault(itemKey, 0);

        if (wonItem.maxRewards() > 0 && alreadyWon >= wonItem.maxRewards()) {
            MessagesManager.sendMessage(getOwner(),
                    Component.text("§cVous avez déjà atteint la limite de cet item : ")
                            .append(Component.text(wonItem.displayName())),
                    Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        for (ItemStack reward : wonItem.rewards()) {
            if (getOwner().getInventory().firstEmpty() != -1) {
                getOwner().getInventory().addItem(reward);
            } else {
                getOwner().getWorld().dropItemNaturally(getOwner().getLocation(), reward);
            }
        }

        if (wonItem.maxRewards() > 0) {
            ps.getMaxItemsGiven().put(itemKey, alreadyWon + 1);
        }

        TicketManager.setTicketGiven(getOwner().getUniqueId(), ps.getTicketRemaining(), ps.isTicketGiven());
        MessagesManager.sendMessage(getOwner(),
                Component.text("§aVous avez gagné : ")
                        .append(Component.text(wonItem.displayName()))
                        .append(Component.text(" §a!")),
                Prefix.OPENMC, MessageType.SUCCESS, true);
    }

    private List<LootItem> initializeLootItems() {
        List<LootItem> items = new ArrayList<>();

        items.add(new LootItem(CustomItemRegistry.getByName("omc_plush:peluche_seinyy").getBest(),
                "§d§lPeluche Seinyy",
                List.of(Component.text("§7Une petite peluche comme Seinyy !")),
                10.0,
                1,
                List.of(CustomItemRegistry.getByName("omc_plush:peluche_seinyy").getBest())));

        items.add(new LootItem(Material.DIAMOND,
                "§b§lDiamants",
                List.of(Component.text("§7Ohhhh mais qu'est ce que c'est précieux ce truc !?")),
                15.0,
                List.of(new ItemStack(Material.DIAMOND, 3))));

        items.add(new LootItem(Material.IRON_INGOT,
                "§7§lLingots de Fer",
                List.of(Component.text("§7Simplement du fer, rien de fou quoi...")),
                20.0,
                List.of(new ItemStack(Material.IRON_INGOT, 10))));

        items.add(new LootItem(Material.NETHERITE_INGOT,
                "§4§lLingot De Netherite",
                List.of(Component.text("§7Le truc le plus rare du jeu !")),
                0.5,
                2,
                List.of(new ItemStack(Material.NETHERITE_INGOT))));

        items.add(new LootItem(Material.OAK_LOG,
                "§6§lBûches de Chêne",
                List.of(Component.text("§7De quoi te faire une petite maison hihi")),
                25.0,
                List.of(new ItemStack(Material.OAK_LOG, 32))));

        items.add(new LootItem(Material.COOKED_BEEF,
                "§c§lSteaks",
                List.of(Component.text("§7Miam miam, de la bonne viande !")),
                15.0,
                List.of(new ItemStack(Material.COOKED_BEEF, 16))));

        items.add(new LootItem(Material.COAL,
                "§8§lCharbon",
                List.of(Component.text("§7De quoi faire du feu")),
                14.5,
                List.of(new ItemStack(Material.COAL, 16))));

        PlayerStats ps = TicketManager.getPlayerStats(getOwner().getUniqueId());
        Iterator<LootItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            LootItem item = iterator.next();
            String itemKey = item.displayName();
            int alreadyWon = ps.getMaxItemsGiven().getOrDefault(itemKey, 0);
            if (item.maxRewards() > 0 && alreadyWon >= item.maxRewards()) {
                iterator.remove();
            }
        }

        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    private void refreshAnimated(List<LootItem> pool) {
        if (!(getOwner().getOpenInventory().getTopInventory().getHolder() instanceof MachineBallsOpenMenu)) return;
        Inventory inv = getOwner().getOpenInventory().getTopInventory();

        for (int i = 0; i < displaySlots.size(); i++) {
            int index;
            LootItem itemToShow;

            if (animationTick > maxAnimationTicks - 10 && i == displaySlots.indexOf(22)) {
                itemToShow = winningItem;
            } else {
                index = (itemOffset + i) % pool.size();
                itemToShow = pool.get(index);
            }

            inv.setItem(displaySlots.get(i), new ItemBuilder(this, itemToShow.material(), meta -> {
                meta.displayName(Component.text(itemToShow.displayName()));
                meta.lore(itemToShow.lore());
            }));
        }

        if (animationTick >= maxAnimationTicks) {
            for (int i : displaySlots) {
                if (i == 22) continue;
                inv.setItem(i, new ItemBuilder(this, Material.GRAY_STAINED_GLASS_PANE).hideTooltip(true));
            }
        }
    }

    private void refresh() {
        if (!(getOwner().getOpenInventory().getTopInventory().getHolder() instanceof MachineBallsOpenMenu)) return;

        Inventory inv = getOwner().getOpenInventory().getTopInventory();
        Map<Integer, ItemBuilder> items = getContent();
        items.forEach(inv::setItem);
    }
}