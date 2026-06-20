package fr.openmc.core.registry.lootboxes;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.events.LootboxRewardEvent;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootboxOpenMenu extends Menu {

    // ** Animation
    private BukkitTask animationTask;
    private boolean isAnimating = false;
    private int animationTick = 0;

    // ** Animation Param
    private final int maxAnimationTicks;
    private final List<Integer> displaySlots;

    private final CustomLootbox box;

    private int itemOffset = 0;
    private CustomLoot winningLoot = null;
    private boolean finished = false;

    public LootboxOpenMenu(@NotNull Player owner, CustomLootbox box) {
        super(owner);
        this.box = box;

        LootboxOptions options = box.getOptions();
        this.maxAnimationTicks = options.animationsTickDuration();
        this.displaySlots = options.displaySlots();

        startAnimation();
    }

    @Override
    public @NotNull Component getName() {
        return box.getName();
    }

    @Override
    public String getTexture() {
        return box.getOptions().textureMenu();
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return box.getOptions().menuSize();
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> items = fill(Material.GRAY_STAINED_GLASS_PANE);

        if (!isAnimating && !finished)
            startAnimation();

        if (finished && winningLoot != null && winningLoot.getRepresentativeItem() != null) {
            ItemStack displayedItem = winningLoot.getRepresentativeItem();

            items.put(box.getOptions().rewardSlot(), new ItemMenuBuilder(this, displayedItem, meta -> {
                meta.displayName(Component.text("§6§l✦ ")
                        .append(displayedItem.effectiveName().decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" §6§l✦")));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("§e§lFÉLICITATIONS !"));
                lore.add(Component.text(" "));
                if (displayedItem.lore() != null)
                    lore.addAll(displayedItem.lore());
                meta.lore(lore);
            }));

            return items;
        }

        List<CustomLoot> weightedPool = box.getLootTable().generateWeightedPool();
        for (int i = 0; i < displaySlots.size(); i++) {
            int lootIndex = (itemOffset + i) % weightedPool.size();
            CustomLoot loot = weightedPool.get(lootIndex);

            ItemStack itemDisplay = loot.getRepresentativeItem();

            if (loot instanceof ItemLoot itemLoot && itemLoot.getDisplayedItem() != null) {
                itemDisplay = itemLoot.getDisplayedItem();
            }

            if (itemDisplay == null) continue;

            ItemStack finalItemDisplay = itemDisplay;

            items.put(displaySlots.get(i), new ItemMenuBuilder(this, itemDisplay, meta -> {
                Component name = finalItemDisplay.effectiveName().decoration(TextDecoration.ITALIC, false);

                if (loot instanceof ItemLoot itemLoot
                        && itemLoot.getMinAmount() != itemLoot.getMaxAmount()) {
                    name = Component.text("§7" + itemLoot.getMinAmount() + "-" + itemLoot.getMaxAmount() + " ")
                            .append(name);
                }

                meta.displayName(name);
                meta.lore(finalItemDisplay.lore());
            }));
        }

        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (animationTask != null && !animationTask.isCancelled()) {
            animationTask.cancel();
            finishAnimation(false);
        }

        Player player = (Player) event.getPlayer();
        player.playSound(Sound.sound(Key.key("minecraft", "block.barrel.close"),
                Sound.Source.BLOCK, 1f, 1f));
    }

    public void startAnimation() {
        if (isAnimating || finished) return;

        isAnimating = true;
        animationTick = 0;

        getOwner().playSound(Sound.sound(Key.key("minecraft", "block.note_block.pling"),
                Sound.Source.BLOCK, 1f, 1f));

        winningLoot = box.getLootTable().selectRandomLoot();

        List<CustomLoot> weightedPool = box.getLootTable().generateWeightedPool();

        animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (animationTick >= maxAnimationTicks) {
                    finishAnimation(true);
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

    private void finishAnimation(boolean withLatency) {
        winningLoot = box.getLootTable().selectRandomLoot();

        finished = true;

        getOwner().playSound(Sound.sound(Key.key("minecraft", "entity.player.levelup"),
                Sound.Source.BLOCK, 1f, 1f));

        refresh();

        new BukkitRunnable() {
            @Override
            public void run() {
                boolean cancelled = giveReward(winningLoot);
                if (cancelled) {
                    cancel();
                    return;
                }
                getOwner().closeInventory();
                if (winningLoot.getChance() <= 0.1) {
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
                                    .append(winningLoot.getRepresentativeItem().effectiveName().decoration(TextDecoration.ITALIC, false))
                                    .append(Component.text(" §eà "))
                                    .append(box.getName())
                                    .append(Component.text(" §e! §6§l✦")),
                            Prefix.OPENMC, MessageType.INFO);
                }
            }
        }.runTaskLater(OMCPlugin.getInstance(), withLatency ? 60L : 0L);
    }

    private boolean giveReward(CustomLoot wonItem) {
        LootboxRewardEvent rewardEvent = new LootboxRewardEvent(getOwner(), box, wonItem);
        Bukkit.getPluginManager().callEvent(rewardEvent);
        if (rewardEvent.isCancelled()) return true;

        wonItem.run(getOwner());

        MessagesManager.sendMessage(getOwner(),
                Component.text("§aVous avez gagné : ")
                        .append(wonItem.getRepresentativeItem().displayName())
                        .append(Component.text(" §a!")),
                Prefix.OPENMC, MessageType.SUCCESS, true);
        return false;
    }

    private void refreshAnimated(List<CustomLoot> pool) {
        if (!(getOwner().getOpenInventory().getTopInventory().getHolder() instanceof LootboxOpenMenu)) return;
        Inventory inv = getOwner().getOpenInventory().getTopInventory();

        for (int i = 0; i < displaySlots.size(); i++) {
            int index;
            ItemStack itemToShow;
            CustomLoot currentLoot;

            if (animationTick > maxAnimationTicks - 10 && i == displaySlots.indexOf(box.getOptions().rewardSlot())) {
                itemToShow = winningLoot.getRepresentativeItem();
                currentLoot = winningLoot;
            } else {
                index = (itemOffset + i) % pool.size();
                currentLoot = pool.get(index);
                itemToShow = currentLoot.getRepresentativeItem();
            }

            ItemStack finalItemToShow = itemToShow;
            CustomLoot finalCurrentLoot = currentLoot;
            inv.setItem(displaySlots.get(i), new ItemMenuBuilder(this, itemToShow, meta -> {
                Component name = finalItemToShow.effectiveName().decoration(TextDecoration.ITALIC, false);

                if (finalCurrentLoot instanceof ItemLoot itemLoot
                        && itemLoot.getMinAmount() != itemLoot.getMaxAmount()) {
                    name = Component.text("§7" + itemLoot.getMinAmount() + "-" + itemLoot.getMaxAmount() + " ")
                            .append(name);
                }

                meta.displayName(name);
                meta.lore(finalItemToShow.lore());
            }));
        }

        if (animationTick >= maxAnimationTicks) {
            for (int i : displaySlots) {
                if (i == box.getOptions().rewardSlot()) continue;
                inv.setItem(i, new ItemMenuBuilder(this, Material.GRAY_STAINED_GLASS_PANE).hideTooltip(true));
            }
        }
    }

    private void refresh() {
        if (!(getOwner().getOpenInventory().getTopInventory().getHolder() instanceof LootboxOpenMenu)) return;

        Inventory inv = getOwner().getOpenInventory().getTopInventory();
        Map<Integer, ItemMenuBuilder> items = getContent();
        items.forEach(inv::setItem);
    }
}
