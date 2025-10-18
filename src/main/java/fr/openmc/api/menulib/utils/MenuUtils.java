package fr.openmc.api.menulib.utils;

import fr.openmc.api.menulib.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class MenuUtils {
	/**
	 * Creates button items in the specified slots of the inventory content map.
	 * @param inventoryContent the map representing the menu content
	 * @param slots the slots where the button items should be placed
	 * @param itemStack the ItemStack to be used as the button item
	 * */
	public static void createButtonItem(Map<Integer, ItemStack> inventoryContent, int[] slots, ItemStack itemStack) {
		for (int slot : slots) {
			inventoryContent.put(slot, itemStack);
		}
	}

	/**
	 * Creates button items in the specified slots of the inventory content map.
	 *
	 * @param inventoryContent the map representing the menu content
	 * @param slots            the slots where the button items should be placed
	 * @param itemBuilder      the ItemBuilder to be used as the button item
	 */
	public static void createButtonItem(Map<Integer, ItemBuilder> inventoryContent, int[] slots, ItemBuilder itemBuilder) {
		for (int slot : slots) {
			inventoryContent.put(slot, itemBuilder);
		}
	}

	/**
	 * Set an Item to be refreshed.
	 * @param player The Player
	 * @param menu The Menu
	 * @param slot Slot of Item
	 * @param itemSupplier Supplier of Item
	 * @return The ItemBuilder with the name set
	 */
	public static BukkitRunnable runDynamicItem(Player player, Menu menu, int slot, Supplier<ItemBuilder> itemSupplier) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if (!menu.getInventory().getHolder().equals(player.getOpenInventory().getTopInventory().getHolder())) {
						cancel();
						return;
					}

					ItemStack item = itemSupplier.get();
					player.getOpenInventory().getTopInventory().setItem(slot, item);
				} catch (Exception e) {
                    throw new RuntimeException(e);
				}
			}
		};
	}

	/**
	 * Set an ButtonItem to be refreshed.
	 *
	 * @param player       The Player
	 * @param menu         The Menu
	 * @param slots        Slot of Item
	 * @param itemSupplier Supplier of Item
	 * @return The ItemBuilder with the name set
	 */
	public static BukkitRunnable runDynamicButtonItem(Player player, Menu menu, int[] slots, Supplier<ItemBuilder> itemSupplier) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if (!menu.getInventory().getHolder().equals(player.getOpenInventory().getTopInventory().getHolder())) {
						cancel();
						return;
					}

					ItemBuilder item = itemSupplier.get();

					for (int slot : slots) {
						player.getOpenInventory().getTopInventory().setItem(slot, item);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	/**
	 * Get the inventory item slots (from 54 to 89)
	 *
	 * @return A list of integers representing the inventory item slots
	 */
	public static List<Integer> getInventoryItemSlots() {
		return IntStream.rangeClosed(54, 89)
				.boxed()
				.toList();
	}
}
