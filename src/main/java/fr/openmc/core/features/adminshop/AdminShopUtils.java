package fr.openmc.core.features.adminshop;

import fr.openmc.core.features.economy.EconomyManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class AdminShopUtils {
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

    public static String getColorNameFromMaterial(Material variant) {
        String name = variant.name();
        if (!name.contains("_")) return "Normal";
        String color = name.split("_")[0];
        return color.substring(0, 1).toUpperCase() + color.substring(1).toLowerCase();
    }

    public static String formatPrice(double price) {
        return String.format("%.2f", price) + " " + EconomyManager.getEconomyIcon();
    }
}
