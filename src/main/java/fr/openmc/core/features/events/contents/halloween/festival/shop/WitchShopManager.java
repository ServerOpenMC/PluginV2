package fr.openmc.core.features.events.contents.halloween.festival.shop;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.adminshop.AdminShopUtils;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.halloween.festival.HalloweenFestivalManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.floor.Floors;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class WitchShopManager {

    public static LinkedHashSet<WitchShopItem> witchStoreItems;
    public static Map<Integer, Set<WitchShopItem>> paginateWitchStoreItems;

    public static int maxPage;

    public static int SLOT_FACTOR = 28;
    private static final Sound BUY_SOUND = Sound.ENTITY_WITCH_CELEBRATE;
    private static final Sound ERROR_SOUND = Sound.ENTITY_WITCH_AMBIENT;

    public static void init() {
        witchStoreItems = getWitchStoreItems();
        paginateWitchStoreItems = getPaginateWitchStoreItems();
        maxPage = getWitchStorePages();
    }

    private static Map<Integer, Set<WitchShopItem>> getPaginateWitchStoreItems() {
        Map<Integer, Set<WitchShopItem>> paginateItems = new HashMap<>();

        List<WitchShopItem> list = new ArrayList<>(witchStoreItems);

        int page = 1;

        for (int i = 0; i < list.size(); i += SLOT_FACTOR) {
            Set<WitchShopItem> subItems = new LinkedHashSet<>(
                    list.subList(i, Math.min(i + SLOT_FACTOR, list.size()))
            );

            paginateItems.put(page++, subItems);
        }

        return paginateItems;
    }

    public static Set<WitchShopItem> getStoreContent(int page) {
        if (paginateWitchStoreItems == null)
            paginateWitchStoreItems = getPaginateWitchStoreItems();

        if (page > maxPage)
            page = maxPage;

        return paginateWitchStoreItems.get(page);
    }

    private static int getWitchStorePages() {
        return paginateWitchStoreItems.size();
    }

    public static void buyItem(Player player, WitchShopItem item, int amount) {
        if (item == null) return;

        if (amount <= 0) {
            sendError(player, TranslationManager.translation("feature.adminshop.amount_sup"));
            return;
        }

        if (!ItemUtils.hasEnoughSpace(player, item.getItem().getBest(), amount)) {
            sendError(player, TranslationManager.translation("feature.adminshop.inventory_full"));
            return;
        }

        int totalPrice = item.getPrice() * amount;
        if (ItemUtils.hasEnoughItems(player, item.getShopMoney().getItem().getBest(), totalPrice)) {

            ItemUtils.giveItem((OMCPlayer) player, item.getItem().getBest(), amount);

            ItemUtils.removeItemsFromPlayerInventory(player, item.getShopMoney().getItem().getBest(), totalPrice);

            sendInfo(player, TranslationManager.translation(
                    "feature.events.halloween.festival.shop.player_buy_item",
                    Component.text(amount),
                    item.getName(),
                    Component.text(totalPrice),
                    item.getShopMoney().getItem().getSprite()
            ));

            player.playSound(player, BUY_SOUND, 1f, new Random().nextFloat(0.6f, 1.4f));

            TerrorHouseManager.updateAdvancement(item.getShopMoney().getScores() * item.getPrice());

            HalloweenFestivalManager.addParticipant(player.getUniqueId());

        } else {
            sendError(player, TranslationManager.translation(
                    "feature.events.halloween.festival.player_not_enough_item",
                    item.getShopMoney().getName()
            ));
        }
    }

    /**
     * Handles the selling of an item by the player.
     *
     * @param player  The player selling the item.
     * @param item  The item sell.
     * @param amount  The quantity to sell.
     */
    public static void sellItem(Player player, ShopMoney item, int amount) {
        if (item == null) return;

        if (amount <= 0) {
            sendError(player, TranslationManager.translation("feature.adminshop.amount_sup"));
            return;
        }

        // Check if the player has enough items to sell
        if (!ItemUtils.hasEnoughItems(player, item.getItem().getBest(), amount)) {
            sendError(player, TranslationManager.translation(
                    "feature.events.halloween.festival.player_not_enough_item",
                    item.getName()
            ));
            return;
        }

        int totalScore = item.getScores() * amount; // Calculate the total price for the items
        ItemUtils.removeItemsFromPlayerInventory(player, item.getItem().getBest(), amount); // Remove items from the player's inventory

        player.playSound(player, BUY_SOUND, 1f, new Random().nextFloat(0.6f, 1.4f));

        TerrorHouseManager.updateAdvancement(totalScore);

        if (totalScore >= 100_000) {

            HalloweenFestivalManager.broadcast(TranslationManager.translation(
                    "feature.events.halloween.festival.deposit.broadcast_add_point",
                    player.displayName(),
                    Component.text(EconomyManager.getFormattedSimplifiedNumber(totalScore)),
                    Component.text(getHalloweenPointIcon()),
                    Component.text(amount),
                    item.getItem().getSprite()
            ).color(NamedTextColor.GOLD));
        } else {
            sendInfo(player, TranslationManager.translation(
                    "feature.events.halloween.festival.deposit.add_point",
                    Component.text(EconomyManager.getFormattedSimplifiedNumber(totalScore)),
                    Component.text(getHalloweenPointIcon()),
                    Component.text(amount),
                    item.getItem().getSprite()
            ));
        }
    }

    private static void sendError(Player player, Component message) {
        MessagesManager.sendMessage(player, message, Prefix.HALLOWEEN, MessageType.ERROR, true);
        player.playSound(player, ERROR_SOUND, 1f,1f);
    }

    private static void sendInfo(Player player, Component message) {
        MessagesManager.sendMessage(player, message, Prefix.HALLOWEEN, MessageType.INFO, true);
    }

    public static List<Component> extractLoreForItem(WitchShopItem item) {
        List<Component> lore = new ArrayList<>();

        if (TerrorHouseManager.enabledFloors.contains(item.getAdvancements())) {
            lore.add(TranslationManager.translation("feature.events.halloween.festival.shop.lore_item.buy",
                    getPrice(item)
            ).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
            lore.add(TranslationManager.translation("feature.events.halloween.festival.shop.lore_item.point",
                    Component.text(item.getShopMoney().getScores() * item.getPrice()),
                    Component.text(getHalloweenPointIcon())
            ).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GOLD));

        }
        else {
            lore.add(TranslationManager.translation("feature.events.halloween.festival.shop.lore_item.lock",
                            Component.text(item.getAdvancements().getSimplifyFloor()))
            );
        }

        return lore;
    }

    public static Component getPrice(WitchShopItem item) {
        return Component.text(item.getPrice())
                .appendSpace()
                .append(item.getShopMoney().getItem().getSprite());
    }

    public static String getHalloweenPointIcon() {
        if (ItemsAdderHook.isEnable()) {
            return FontImageWrapper.replaceFontImages("§f:halloween_point:");
        } else {
            return "Ⓟ";
        }
    }

    private static LinkedHashSet<WitchShopItem> getWitchStoreItems() {
        LinkedHashSet<WitchShopItem> items = new LinkedHashSet<>();

        items.add(new WitchShopItem(Floors.FLOOR_1, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_CANDLE, 10));
        items.add(new WitchShopItem(Floors.FLOOR_1, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_PACK_PUMPKIN, 10));

        items.add(new WitchShopItem(Floors.FLOOR_2, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_PUMPKIN_1, 15));
        items.add(new WitchShopItem(Floors.FLOOR_2, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_PACK_WOOD_CROSS, 15));

        items.add(new WitchShopItem(Floors.FLOOR_3, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_SKULL_JAR, 20));
        items.add(new WitchShopItem(Floors.FLOOR_3, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_PACK_TOMBE_1, 20));

        items.add(new WitchShopItem(Floors.FLOOR_4, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_PUMPKIN_2, 25));
        items.add(new WitchShopItem(Floors.FLOOR_4, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_PACK_CAULDRON, 25));

        items.add(new WitchShopItem(Floors.FLOOR_5, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_SPIDER_IN_JAR, ShopMoney.GREEN_CANDY, 5));

        items.add(new WitchShopItem(Floors.FLOOR_6, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_ROSE_IN_GLASS, ShopMoney.GREEN_CANDY, 10));

        items.add(new WitchShopItem(Floors.FLOOR_7, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_PICTURE, ShopMoney.GREEN_CANDY, 10));
        items.add(new WitchShopItem(Floors.FLOOR_7, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_PACK_TOMBE_2, ShopMoney.GREEN_CANDY, 10));

        items.add(new WitchShopItem(Floors.FLOOR_8, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_PUMPKIN_3, ShopMoney.BLUE_CANDY, 5));
        items.add(new WitchShopItem(Floors.FLOOR_8, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_PACK_COFFIN_OPEN, ShopMoney.BLUE_CANDY, 5));

        items.add(new WitchShopItem(Floors.FLOOR_9, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_WALL_LAMP, ShopMoney.BLUE_CANDY, 5));
        items.add(new WitchShopItem(Floors.FLOOR_9, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_PACK_TOILE_2, ShopMoney.BLUE_CANDY, 5));

        items.add(new WitchShopItem(Floors.FLOOR_10, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_WITCH_POT, ShopMoney.BLUE_CANDY, 10));
        items.add(new WitchShopItem(Floors.FLOOR_10, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_PACK_PUMPKIN_HAT, ShopMoney.BLUE_CANDY, 10));

        items.add(new WitchShopItem(Floors.FLOOR_11, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_SCARECROW, 10));

        items.add(new WitchShopItem(Floors.FLOOR_12, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_TOMB_STONE_1, ShopMoney.RED_CANDY, 2));

        items.add(new WitchShopItem(Floors.FLOOR_13, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_SKULL_SUN_FLOWER, ShopMoney.RED_CANDY, 2));

        items.add(new WitchShopItem(Floors.FLOOR_14, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_TOMB_STONE_2, ShopMoney.RED_CANDY, 2));
        items.add(new WitchShopItem(Floors.FLOOR_14, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_PACK_COFFIN_CLOSE, ShopMoney.RED_CANDY, 2));

        items.add(new WitchShopItem(Floors.FLOOR_15, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_TRICK_OR_TREAT_BOWL, ShopMoney.RED_CANDY, 3));
        items.add(new WitchShopItem(Floors.FLOOR_15, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_BROOM, ShopMoney.RED_CANDY, 3));
        items.add(new WitchShopItem(Floors.FLOOR_15, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_CELLING_LAMP, ShopMoney.RED_CANDY, 3));
        items.add(new WitchShopItem(Floors.FLOOR_15, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_MR_THING, ShopMoney.RED_CANDY, 4));
        items.add(new WitchShopItem(Floors.FLOOR_15, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_PUMPKIN_4, ShopMoney.RED_CANDY, 4));
        items.add(new WitchShopItem(Floors.FLOOR_15, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_DECORATION_V1_SMILE_FLAG, ShopMoney.RED_CANDY, 4));
        items.add(new WitchShopItem(Floors.FLOOR_15, OMCRegistry.CUSTOM_ITEMS.HALLOWEEN_PACK_TOILE_1, ShopMoney.RED_CANDY, 4));

        return items;
    }
}
