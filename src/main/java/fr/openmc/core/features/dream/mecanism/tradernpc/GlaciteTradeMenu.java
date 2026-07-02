package fr.openmc.core.features.dream.mecanism.tradernpc;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.events.GlaciteTradeEvent;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.dream.trader.menu.name");
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
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> inventory = new HashMap<>();
        Player player = getOwner();

        List<Integer> tradeSlots = new ArrayList<>(Arrays.asList(11, 12, 13, 14, 15, 21, 22, 23));
        GlaciteTrade[] trades = GlaciteTrade.values();

        for (int i = 1; i <= trades.length && i < tradeSlots.size(); i++) {
            GlaciteTrade trade = trades[i - 1];
            int slot = tradeSlots.get(i);

            inventory.put(slot,
                    new ItemMenuBuilder(this, trade.getResult().getBest(), meta -> {
                        meta.itemName(trade.getDisplayName());
                        meta.lore(this.getLoreTrade(trade));
                    }).setOnClick(event -> handleTrade(player, trade))
            );
        }


        int timeSlot = tradeSlots.getFirst();

        List<Component> loreTime = TranslationManager.translationLore(
                "feature.dream.trader.menu.time.lore",
                Component.text(1).color(NamedTextColor.DARK_PURPLE)
        );

        inventory.put(timeSlot,
                new ItemMenuBuilder(this, Material.EXPERIENCE_BOTTLE, meta -> {
                    meta.itemName(TranslationManager.translation("feature.dream.trader.menu.time.name"));
                    meta.lore(loreTime);
                }).setOnClick(event -> {
                    ItemStack eweniteItem = DreamItemRegistry.EWENITE.getBest();
                    int ewenite = ItemUtils.countItems(player, eweniteItem);

                    if (ewenite < 1) {
                        MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.trader.message.not_enough_resources"), Prefix.DREAM, MessageType.ERROR, false);
                        return;
                    }

                    ItemUtils.removeItemsFromInventory(player, eweniteItem, 1);

                    DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
                    if (dreamPlayer == null) return;
                    dreamPlayer.addTime(60L);

                    MessagesManager.sendMessage(player, TranslationManager.translation(
                            "feature.dream.trader.message.time_trade_success",
                            Component.text(1).color(NamedTextColor.DARK_PURPLE),
                            TranslationManager.translation("feature.dream.trader.time.one_minute").color(NamedTextColor.GREEN)
                    ), Prefix.DREAM, MessageType.SUCCESS, false);
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
        ItemStack glaciteItem = DreamItemRegistry.GLACITE.getBest();
        ItemStack eweniteItem = DreamItemRegistry.EWENITE.getBest();
        int glacite = ItemUtils.countItems(player, glaciteItem);
        int ewenite = ItemUtils.countItems(player, eweniteItem);

        int tradeGlacite = trade.getGlaciteCost();
        int tradeEwenite = trade.getEweniteCost();

        if (glacite < tradeGlacite || ewenite < tradeEwenite) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.trader.message.not_enough_resources"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        ItemUtils.removeItemsFromInventory(player, glaciteItem, tradeGlacite);
        ItemUtils.removeItemsFromInventory(player, eweniteItem, tradeEwenite);

        player.getInventory().addItem(trade.getResult().getBest());

        Component cost = Component.empty();
        if (tradeGlacite > 0) {
            cost = cost.append(TranslationManager.translation(
                    "feature.dream.trader.message.cost.glacite",
                    Component.text(tradeGlacite).color(NamedTextColor.AQUA)
            ));
        }
        if (tradeEwenite > 0) {
            if (tradeGlacite > 0) {
                cost = cost.appendSpace()
                        .append(TranslationManager.translation("feature.dream.trader.message.cost.separator"))
                        .appendSpace();
            }
            cost = cost.append(TranslationManager.translation(
                    "feature.dream.trader.message.cost.ewenite",
                    Component.text(tradeEwenite).color(NamedTextColor.DARK_PURPLE)
            ));
        }

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getServer().getPluginManager().callEvent(new GlaciteTradeEvent(player, trade))
        );
        MessagesManager.sendMessage(player, TranslationManager.translation(
                "feature.dream.trader.message.trade_success",
                cost,
                trade.getDisplayName()
        ), Prefix.DREAM, MessageType.SUCCESS, false);
    }

    private List<Component> getLoreTrade(GlaciteTrade trade) {
        List<Component> lore = new ArrayList<>();

        lore.addAll(TranslationManager.translationLore("feature.dream.trader.menu.trade.lore.cost"));

        if (trade.getGlaciteCost() > 0)
            lore.addAll(TranslationManager.translationLore(
                    "feature.dream.trader.menu.trade.lore.glacite",
                    Component.text(trade.getGlaciteCost()).color(NamedTextColor.AQUA)
            ));

        if (trade.getEweniteCost() > 0)
            lore.addAll(TranslationManager.translationLore(
                    "feature.dream.trader.menu.trade.lore.ewenite",
                    Component.text(trade.getEweniteCost()).color(NamedTextColor.DARK_PURPLE)
            ));

        lore.add(Component.text(""));
        lore.addAll(TranslationManager.translationLore("feature.dream.trader.menu.trade.lore.click"));

        return lore;
    }
}
