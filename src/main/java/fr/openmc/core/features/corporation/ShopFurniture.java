package fr.openmc.core.features.corporation;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.utils.world.Yaw;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class ShopFurniture {
	
	public static boolean placeShopFurniture(Block block, Yaw playerYaw) {
		CustomStack customFurniture = CustomFurniture.getInstance("omc_company:caisse");
		if (customFurniture == null || block.getType() != Material.AIR) return false;
		
		CustomFurniture furniture = CustomFurniture.spawn("omc_company:caisse", block);
		furniture.getEntity().setRotation(playerYaw.getPlayerYaw(), 0);
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