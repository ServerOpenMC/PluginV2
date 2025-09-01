package fr.openmc.core.disabled.corporation;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.block.Block;

public class ItemsAdderIntegration {

    public static boolean placeShopFurniture(Block block) {
        CustomStack customFurniture = CustomFurniture.getInstance("omc_company:caisse");
        if (customFurniture == null || block.getType() != org.bukkit.Material.AIR)
            return false;

        CustomFurniture.spawn("omc_company:caisse", block);
        return true;
    }

    public static boolean removeShopFurniture(Block block) {
        CustomStack placed = CustomFurniture.byAlreadySpawned(block);
        if (placed == null || !placed.getNamespacedID().equals("omc_company:caisse"))
            return false;

        CustomFurniture.remove(CustomFurniture.byAlreadySpawned(block).getEntity(), false);
        return true;
    }

    public static boolean hasFurniture(Block block) {
        CustomStack placed = CustomFurniture.byAlreadySpawned(block);
        return placed != null && placed.getNamespacedID().equals("omc_company:caisse");
    }

}
