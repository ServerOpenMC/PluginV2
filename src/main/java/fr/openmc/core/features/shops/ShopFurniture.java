package fr.openmc.core.features.shops;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.world.Yaw;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class ShopFurniture {
	
	/**
	 * Places a specific type of shop furniture at the given block location using ItemsAdder API, oriented
	 * according to the player's yaw direction.
	 *
	 * @param block The block where the shop furniture should be placed. Must be of type AIR.
	 * @param playerYaw The yaw direction of the player, used to determine the orientation
	 *                  of the furniture.
	 * @return true if the furniture was successfully placed, false otherwise.
	 */
	public static boolean placeShopFurniture(Block block, Yaw playerYaw) {
		CustomStack customFurniture = CustomFurniture.getInstance(OMCRegistry.CUSTOM_ITEMS.CAISSE.getId());
		if (customFurniture == null || block.getType() != Material.AIR) return false;
		
		CustomFurniture furniture = CustomFurniture.spawn(OMCRegistry.CUSTOM_ITEMS.CAISSE.getId(), block);
		furniture.getEntity().setRotation(playerYaw.getPlayerYaw(), 0);
		return true;
	}
	
	/**
	 * Removes a specific type of shop furniture at the given block location using ItemsAdder API.
	 *
	 * @param block The block where the shop furniture is placed.
	 * @return true if the furniture was successfully removed, false otherwise.
	 */
	public static boolean removeShopFurniture(Block block) {
		CustomStack placed = CustomFurniture.byAlreadySpawned(block);
		if (placed == null || !placed.getNamespacedID().equals(OMCRegistry.CUSTOM_ITEMS.CAISSE.getId())) return false;
		
		CustomFurniture.remove(CustomFurniture.byAlreadySpawned(block).getEntity(), false);
		return true;
	}
	
	/**
	 * Checks if the specified block contains a specific type of shop furniture.
	 *
	 * @param block The block to check for shop furniture. Must not be null.
	 * @return true if the block contains the shop furniture, false otherwise.
	 */
	public static boolean hasFurniture(Block block) {
		CustomStack placed = CustomFurniture.byAlreadySpawned(block);
		return placed != null && placed.getNamespacedID().equals(OMCRegistry.CUSTOM_ITEMS.CAISSE.getId());
	}
	
}