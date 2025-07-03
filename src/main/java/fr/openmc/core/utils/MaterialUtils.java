package fr.openmc.core.utils;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class MaterialUtils {

    private static final Set<Material> BUNDLES = Tag.ITEMS_BUNDLES.getValues();

    /**
     * Retourne si l'Item est un Bundle
     * @param item L'ItemStack à tester
     */
    public static boolean isBundle(ItemStack item) {
        return BUNDLES.contains(item.getType());
    }

    private static final Set<Material> CROPS = Tag.CROPS.getValues();

    public static boolean isCrop(Material type) {
        return CROPS.contains(type);
    }

    private static final Set<Material> ORES = Set.of(
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.COPPER_ORE,
            Material.GOLD_ORE,
            Material.REDSTONE_ORE,
            Material.LAPIS_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.NETHER_QUARTZ_ORE,
            Material.NETHER_GOLD_ORE,
            Material.DEEPSLATE_COAL_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.DEEPSLATE_EMERALD_ORE
    );

    public static boolean isOre(Material type) {
        return ORES.contains(type);
    }

    private static final Set<String> CUSTOM_CROPS = Set.of(
            "omc_foods:tomato_seeds",
            "omc_foods:onion_seeds",
            "omc_foods:salad_seed",
            "omc_foods:courgette_seed"
    );

    public static boolean isCustomCrop(String namespace) {
        return CUSTOM_CROPS.contains(namespace);
    }

    private static final Set<Material> CONTAINERS = Set.of(
            Material.CHEST,
            Material.CHEST_MINECART,
            Material.SHULKER_BOX,
            Material.WHITE_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.PINK_SHULKER_BOX,
            Material.RED_SHULKER_BOX,
            Material.PURPLE_SHULKER_BOX,
            Material.BARREL,
            Material.DECORATED_POT
    );

    public static boolean isContainers(Material type) {
        return CONTAINERS.contains(type);
    }
}
