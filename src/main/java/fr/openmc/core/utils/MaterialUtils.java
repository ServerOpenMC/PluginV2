package fr.openmc.core.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MaterialUtils {
    /**
     * Retourne si l'Item est un Bundle
     * @param item L'ItemStack à tester
     */
    public static boolean isBundle(ItemStack item) {
        List<Material> bundles = List.of(
                Material.BUNDLE,
                Material.WHITE_BUNDLE,
                Material.BLUE_BUNDLE,
                Material.BROWN_BUNDLE,
                Material.CYAN_BUNDLE,
                Material.GRAY_BUNDLE,
                Material.GREEN_BUNDLE,
                Material.LIME_BUNDLE,
                Material.MAGENTA_BUNDLE,
                Material.ORANGE_BUNDLE,
                Material.YELLOW_BUNDLE,
                Material.LIGHT_BLUE_BUNDLE,
                Material.LIGHT_GRAY_BUNDLE,
                Material.PINK_BUNDLE,
                Material.RED_BUNDLE,
                Material.PURPLE_BUNDLE
        );

        return bundles.contains(item.getType());
    }

    public static boolean isCrop(Material type) {
        List<Material> crops = List.of(
                Material.WHEAT,
                Material.CARROTS,
                Material.POTATOES,
                Material.BEETROOTS,
                Material.NETHER_WART,
                Material.COCOA,
                Material.TORCHFLOWER,
                Material.PITCHER_CROP
        );

        return crops.contains(type);
    }
}
