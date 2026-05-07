package fr.openmc.core.features.adminshop;

import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;

public class AdminShopUtils {

    /**
     * Generates the lore (description) for an item in the admin shop.
     * This includes buy/sell prices and interaction instructions.
     *
     * @param item The shop item to generate the lore for.
     * @return A list of {@link Component} representing the item's lore.
     */
    public static List<Component> extractLoreForItem(ShopItem item) {
        List<Component> lore = new ArrayList<>();
        boolean buy = item.getInitialBuyPrice() > 0;
        boolean sell = item.getInitialSellPrice() > 0;

        if (buy) lore.add(TranslationManager.translation("feature.adminshop.lore_item.buy",
                Component.text(formatPrice(item.getActualBuyPrice()))
        ).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        if (sell) lore.add(TranslationManager.translation("feature.adminshop.lore_item.sell",
                Component.text(formatPrice(item.getActualSellPrice()))
        ).color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));

        lore.add(Component.empty());

        if(buy) lore.add(TranslationManager.translation("feature.adminshop.right_click_buy"));
        if (sell) lore.add(TranslationManager.translation("feature.adminshop.left_click_sell"));

        return lore;
    }

    /**
     * Formats a price to a readable string with two decimal places and the economy icon.
     *
     * @param price The price to format.
     * @return A string representation of the price, including the economy icon.
     */
    public static String formatPrice(double price) {
        return String.format("%.2f", price) + " " + EconomyManager.getEconomyIcon();
    }
}
