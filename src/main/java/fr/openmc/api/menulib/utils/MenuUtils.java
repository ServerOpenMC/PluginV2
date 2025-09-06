package fr.openmc.api.menulib.utils;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import fr.openmc.api.menulib.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.function.Supplier;

public class MenuUtils {
	
	/**
	 * Get the navigation buttons
	 * @return Return a list with the navigation buttons (index 0 = back, index 1 = cancel, index 2 = next)
	 */
	public static ArrayList<ItemBuilder> getNavigationButtons(Menu menu) {
		
		ArrayList<ItemBuilder> navigationButtons = new ArrayList<>();
		
		String previousName = "§cPrécédent";
		String cancelName = "§cAnnuler";
		String nextName = "§aSuivant";
		
		for (CustomStack customStack : ItemsAdder.getAllItems("_iainternal")) {
			if (customStack.getNamespacedID().equals("_iainternal:icon_back_orange")) {
				navigationButtons.addFirst(itemBuilderSetName(new ItemBuilder(menu, customStack.getItemStack()), previousName));
			} else if (customStack.getNamespacedID().equals("_iainternal:icon_cancel")) {
				navigationButtons.add(1, itemBuilderSetName(new ItemBuilder(menu, customStack.getItemStack()), cancelName));
			} else if (customStack.getNamespacedID().equals("_iainternal:icon_next_orange")) {
				navigationButtons.addLast(itemBuilderSetName(new ItemBuilder(menu, customStack.getItemStack()), nextName));
			}
		}
		
		if (navigationButtons.size() != 3) {
			navigationButtons.add(itemBuilderSetName(new ItemBuilder(menu, Material.RED_WOOL), previousName));
			navigationButtons.add(itemBuilderSetName(new ItemBuilder(menu, Material.BARRIER), cancelName));
			navigationButtons.add(itemBuilderSetName(new ItemBuilder(menu, Material.GREEN_WOOL), nextName));
		}
		
		return navigationButtons;
	}
	
	/**
	 * Set the name of an ItemBuilder
	 * @param itemBuilder The ItemBuilder
	 * @param name The name
	 * @return The ItemBuilder with the name set
	 */
	private static ItemBuilder itemBuilderSetName(ItemBuilder itemBuilder, String name) {
		ItemMeta itemMeta = itemBuilder.getItemMeta();
		itemMeta.setDisplayName(name);
		itemBuilder.setItemMeta(itemMeta);
		
		return itemBuilder;
	}

	/**
	 * Set an Item to be refreshed.
	 * [ATTENTION METTRE UN NOM DIFFERENT DES AUTRES MENUS]
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
					e.printStackTrace();
				}
			}
		};
	}
}
