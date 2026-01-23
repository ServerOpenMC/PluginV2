package fr.openmc.core.features.dream.mecanism.tradernpc;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.events.GlaciteTradeEvent;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GlaciteTradeMenu extends Menu {
    public GlaciteTradeMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Trades du Vagabond";
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        // empty
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();

        List<Integer> tradeSlots = new ArrayList<>(Arrays.asList(11, 12, 13, 14, 15, 21, 22, 23));
        GlaciteTrade[] trades = GlaciteTrade.values();

        for (int i = 1; i <= trades.length && i < tradeSlots.size(); i++) {
            GlaciteTrade trade = trades[i - 1];
            int slot = tradeSlots.get(i);

            inventory.put(slot,
                    new ItemBuilder(this, trade.getResult().getBest(), meta -> {
                        meta.itemName(trade.getDisplayName());
                        meta.lore(this.getLoreTrade(trade));
                    }).setOnClick(event -> handleTrade(player, trade))
            );
        }


        int timeSlot = tradeSlots.getFirst();

        List<Component> loreTime = List.of(
                Component.text("§7Achetez du temps pour vous permettre de rester plus longtemps !"),
                Component.text("§7Coût :"),
                Component.text(" §51 Ewenite")
        );

        inventory.put(timeSlot,
                new ItemBuilder(this, Material.EXPERIENCE_BOTTLE, meta -> {
                    meta.itemName(Component.text("1 min de Temps", NamedTextColor.GREEN));
                    meta.lore(loreTime);
                }).setOnClick(event -> {
                    ItemStack eweniteItem = DreamItemRegistry.getByName("omc_dream:ewenite").getBest();
                    int ewenite = ItemUtils.countItems(player, eweniteItem);

                    if (ewenite < 1) {
                        MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez de ressources pour effectuer cet achat !"), Prefix.DREAM, MessageType.ERROR, false);
                        return;
                    }

                    ItemUtils.removeItemsFromInventory(player, eweniteItem, 1);

                    DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
                    if (dreamPlayer == null) return;
                    dreamPlayer.addTime(60L);

                    MessagesManager.sendMessage(player, Component.text("Vous avez échangé §51 d'Ewenite §fcontre §a1 minute de temps"), Prefix.DREAM, MessageType.SUCCESS, false);
                })
        );

        return inventory;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    private void handleTrade(Player player, GlaciteTrade trade) {
        ItemStack glaciteItem = DreamItemRegistry.getByName("omc_dream:glacite").getBest();
        ItemStack eweniteItem = DreamItemRegistry.getByName("omc_dream:ewenite").getBest();
        int glacite = ItemUtils.countItems(player, glaciteItem);
        int ewenite = ItemUtils.countItems(player, eweniteItem);

        int tradeGlacite = trade.getGlaciteCost();
        int tradeEwenite = trade.getEweniteCost();

        if (glacite < tradeGlacite || ewenite < tradeEwenite) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez de ressources pour effectuer cet achat !"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        ItemUtils.removeItemsFromInventory(player, glaciteItem, tradeGlacite);
        ItemUtils.removeItemsFromInventory(player, eweniteItem, tradeEwenite);

        player.getInventory().addItem(trade.getResult().getBest());

        String sb = "Vous avez échangé ";

        if (tradeGlacite > 0) {
            sb += glacite + " §bde Glacite§f";
        }

        if (tradeEwenite > 0) {
            if (tradeGlacite > 0) sb += " et ";
            sb += ewenite + " §5d'Ewenite§f";
        }

        sb += " contre §b" + PlainTextComponentSerializer.plainText().serialize(trade.getDisplayName());

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getServer().getPluginManager().callEvent(new GlaciteTradeEvent(player, trade))
        );
        MessagesManager.sendMessage(player, Component.text(sb), Prefix.DREAM, MessageType.SUCCESS, false);
    }

    private List<Component> getLoreTrade(GlaciteTrade trade) {
        List<Component> lore = new ArrayList<>();

        lore.add(Component.text("§7Coût :"));

        if (trade.getGlaciteCost() > 0)
            lore.add(Component.text(" §b" + trade.getGlaciteCost() + " Glacite"));

        if (trade.getEweniteCost() > 0)
            lore.add(Component.text(" §5" + trade.getEweniteCost() + " Ewenite"));

        lore.add(Component.text(""));
        lore.add(Component.text("§e§lCLIQUE-GAUCHE POUR ACHETER"));

        return lore;
    }
}
