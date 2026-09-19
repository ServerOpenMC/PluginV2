package fr.openmc.core.features.events.contents.halloween.halloween.shop;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.features.adminshop.AdminShopUtils;
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

    //TODO idée :
    // bruit de sorcière quand on buy un item

    public static Set<WitchShopItem> witchStoreItems;
    public static Map<Integer, Set<WitchShopItem>> paginateWitchStoreItems;

    public static int maxPage;

    public static int SLOT_FACTOR = 28;
    private static final Sound BUY_SOUND = Sound.ENTITY_WITCH_CELEBRATE;

    public void init() {
        witchStoreItems = getWitchStoreItems();
        paginateWitchStoreItems = getPaginateWitchStoreItems();
        maxPage = getWitchStorePages();
    }

    private static Set<WitchShopItem> getWitchStoreItems() {
        return Set.of(
                new WitchShopItem()
        );
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
        return witchStoreItems.size() / SLOT_FACTOR;
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

        if (item.getPrice() <= 0) {
            sendError(player, TranslationManager.translation("feature.adminshop.item_not_sellable"));
            return;
        }

        int totalPrice = item.getPrice() * amount;
        if (ItemUtils.hasEnoughItems(player, item.getItem().getBest(), totalPrice)) {

            ItemUtils.giveItem(player, item.getItem().getBest(), amount);

            sendInfo(player, TranslationManager.translation("feature.adminshop.player_buy_item",
                    Component.text(amount), item.getName(), Component.text(AdminShopUtils.formatPrice(totalPrice))
            ));

            player.playSound(player, BUY_SOUND, 1f, 1f); //TODO random pitch
        } else {
            sendError(player, TranslationManager.translation("feature.adminshop.have_enough_money")); //TODO
        }
    }

    private static void sendError(Player player, Component message) {
        MessagesManager.sendMessage(player, message, Prefix.ADMINSHOP, MessageType.ERROR, true); //TODO prefix
    }

    private static void sendInfo(Player player, Component message) {
        MessagesManager.sendMessage(player, message, Prefix.ADMINSHOP, MessageType.INFO, true); //TODO prefix
    }

    public static List<Component> extractLoreForItem(WitchShopItem item) {
        List<Component> lore = new ArrayList<>();

        if (true) {
            lore.add(TranslationManager.translation("feature.adminshop.lore_item.buy", //TODO dire a qu'elle room on le débloque
                    Component.text(item.getAdvancements().name())
            ).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        } // TODO check the item advancement and compare with the currents
        else {
            lore.add(TranslationManager.translation("feature.adminshop.lore_item.buy", //TODO
                    Component.text(getPrice(item))
            ).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        }

        return lore;
    }

    public static String getPrice(WitchShopItem item) {
        return item.getPrice() + " " + item.getShopMoney();
    }

    public static String getMoneyItemIcon(String icon) {
        if (ItemsAdderHook.isEnable()) {
            return FontImageWrapper.replaceFontImages("§f:"+icon+":"); //TODO omc_icons
        } else {
            return "Ⓐ";
        }
    }
}
