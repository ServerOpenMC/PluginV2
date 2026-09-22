package fr.openmc.core.features.events.contents.halloween.halloween.shop;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.adminshop.AdminShopUtils;
import fr.openmc.core.features.events.contents.halloween.halloween.HalloweenManager;
import fr.openmc.core.features.events.contents.halloween.halloween.dimension.advancement.Advancements;
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

            List<WitchShopItem> subList = list.subList(i, Math.min(i + SLOT_FACTOR, list.size()));

            paginateItems.put(page++, new HashSet<>(subList));
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
        return (int) Math.ceil((double) witchStoreItems.size() / SLOT_FACTOR);
    }

    public static void buyItem(Player player, WitchShopItem item, int amount) {
        if (item == null) return;

        if (amount <= 0) {
            sendError(player, TranslationManager.translation("feature.adminshop.amount_sup")); //TODO
            return;
        }

        if (!ItemUtils.hasEnoughSpace(player, item.getItem().getBest(), amount)) {
            sendError(player, TranslationManager.translation("feature.adminshop.inventory_full")); //TODO
            return;
        }

        if (item.getPrice() <= 0) {
            sendError(player, TranslationManager.translation("feature.adminshop.item_not_sellable")); //TODO
            return;
        }

        int totalPrice = item.getPrice() * amount;
        if (ItemUtils.hasEnoughItems(player, item.getItem().getBest(), totalPrice)) {

            ItemUtils.giveItem(player, item.getItem().getBest(), amount);

            sendInfo(player, TranslationManager.translation("feature.adminshop.player_buy_item", //TODO
                    Component.text(amount), item.getName(), Component.text(AdminShopUtils.formatPrice(totalPrice))
            ));

            player.playSound(player, BUY_SOUND, 1f, new Random().nextFloat(0.6f, 1.4f));
        } else {
            sendError(player, TranslationManager.translation("feature.adminshop.have_enough_money")); //TODO
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

        if (HalloweenManager.hasUnlock(item.getAdvancements())) {
            lore.add(TranslationManager.translation("feature.adminshop.lore_item.buy", //TODO
                    getPrice(item)
            ).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));

        }
        else {
            lore.add(TranslationManager.translation("feature.adminshop.lore_item.buy." + item.getAdvancements().name().toLowerCase()) //TODO change key
            .color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        }

        return lore;
    }

    public static Component getPrice(WitchShopItem item) {
        return Component.text(item.getPrice() + " " + item.getShopMoney().getIcon());
    }

    public static String getMoneyItemIcon(String icon) {
        if (ItemsAdderHook.isEnable()) {
            return FontImageWrapper.replaceFontImages("§f:"+icon+":");
        } else {
            return "Ⓐ"; //TODO Translationmanager.translate("<...>." + icon)
        }
    }

    //TODO mettre les vrais items
    private static LinkedHashSet<WitchShopItem> getWitchStoreItems() {
        LinkedHashSet<WitchShopItem> items = new LinkedHashSet<>();

        items.add(new WitchShopItem(
                Advancements.ROOM_1, OMCRegistry.CUSTOM_ITEMS.AYWENITE, 10));

        items.add(new WitchShopItem(
                Advancements.ROOM_2, OMCRegistry.CUSTOM_ITEMS.KEBAB, 10));

        items.add(new WitchShopItem(
                Advancements.ROOM_2, OMCRegistry.CUSTOM_ITEMS.NETHERITE_HAMMER, 1000));

        return items;
    }
}
