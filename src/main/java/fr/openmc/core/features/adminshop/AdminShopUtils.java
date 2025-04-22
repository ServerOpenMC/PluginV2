package fr.openmc.core.features.adminshop;

import fr.openmc.core.features.economy.EconomyManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

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

        if (buy) lore.add(Component.text("§aAcheter: " + formatPrice(item.getActualBuyPrice())));
        if (sell) lore.add(Component.text("§cVendre: " + formatPrice(item.getActualSellPrice())));
        lore.add(Component.text("§7"));

        if (item.isHasColorVariant()) {
            lore.add(Component.text("§8■ §7Clique milieu pour choisir une couleur"));
        } else {
            if (buy) lore.add(Component.text("§8■ §aClique gauche pour §2acheter"));
            if (sell) lore.add(Component.text("§8■ §cClique droit pour §4vendre"));
        }

        return lore;
    }

    /**
     * Extracts the color name from a given {@link Material}.
     * For example, "RED_WOOL" will return "Red".
     *
     * @param variant The material to extract the color from.
     * @return A capitalized color name, or "Normal" if no color is found.
     */
    public static String getColorNameFromMaterial(Material variant) {
        String name = variant.name();
        if (!name.contains("_")) return "Normal";
        String color = name.split("_")[0];
        return color.substring(0, 1).toUpperCase() + color.substring(1).toLowerCase();
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
