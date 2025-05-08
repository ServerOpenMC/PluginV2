package fr.openmc.api.menulib;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Made by Xernas
 * <p>
 * {@code MenuLib} is a utility class designed to handle custom menus in a Bukkit/Spigot plugin environment.
 * It provides functionality for managing menu interactions, associating click events with specific items,
 * and handling player-menu associations.
 * <p>
 * This class is intended for use in creating interactive menus within Minecraft plugins,
 * allowing developers to define custom behavior for item clicks within menus.
 * <p>
 * The {@code MenuLib} class implements the {@link Listener} interface to handle inventory-related events.
 */
public final class MenuLib implements Listener {
	
	private static final Map<Player, Menu> lastMenu = new HashMap<>();
	private static final Map<Menu, Map<ItemStack, Consumer<InventoryClickEvent>>> itemClickEvents = new HashMap<>();
	@Getter
	private static NamespacedKey itemIdKey;
	
	/**
	 * Constructs a new {@code MenuLib} instance and registers it as an event listener.
	 * Also initializes the {@link NamespacedKey} used for item identification.
	 *
	 * @param plugin The {@link JavaPlugin} instance used to register events and create the {@link NamespacedKey}
	 */
	private MenuLib(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		itemIdKey = new NamespacedKey(plugin, "itemId");
	}
	
	/**
	 * Initializes the {@code MenuLib} library for the given plugin.
	 * This method sets up necessary event handling and utilities
	 * required for managing custom menus.
	 *
	 * @param plugin The {@link JavaPlugin} instance representing the plugin
	 *               that integrates the {@code MenuLib} library. This is used
	 *               to register event listeners and initialize key functionality.
	 */
	public static void init(JavaPlugin plugin) {
		new MenuLib(plugin);
	}
	
	/**
	 * Associates a click event handler with a specific item in a given menu.
	 * When a player clicks on the specified {@link ItemStack} in the menu,
	 * the provided {@link Consumer} is executed to handle the {@link InventoryClickEvent}.
	 *
	 * @param menu      The {@link Menu} in which the click event will be associated.
	 * @param itemStack The {@link ItemStack} that will trigger the event when clicked.
	 * @param e         A {@link Consumer} of {@link InventoryClickEvent} representing the event handler
	 *                  to be executed when the {@link ItemStack} is clicked within the menu.
	 */
	public static void setItemClickEvent(Menu menu, ItemStack itemStack, Consumer<InventoryClickEvent> e) {
		Map<ItemStack, Consumer<InventoryClickEvent>> itemEvents = itemClickEvents.get(menu);
		if (itemEvents == null) {
			itemEvents = new HashMap<>();
		}
		itemEvents.put(itemStack, e);
		itemClickEvents.put(menu, itemEvents);
	}
	
	/**
	 * Sets the last menu viewed or interacted with by the specified player.
	 * This method associates the given player with a menu, allowing retrieval
	 * of the last accessed menu via {@link MenuLib#getLastMenu(Player)}.
	 *
	 * @param player The {@link Player} for whom the last menu is being set.
	 * @param menu   The {@link Menu} object to be associated as the player's last menu.
	 */
	public static void setLastMenu(Player player, Menu menu) {
		lastMenu.put(player, menu);
	}
	
	/**
	 * Retrieves the last menu associated with the specified player, if any.
	 * This method returns the menu that was last viewed or interacted with
	 * by the given player.
	 *
	 * @param player The {@link Player} for whom the last menu is to be retrieved.
	 * @return The {@link Menu} object that was last associated with the player,
	 * or {@code null} if no menu is associated with the player.
	 */
	public static Menu getLastMenu(Player player) {
		return lastMenu.get(player);
	}
	
	/**
	 * Handles click events in an inventory associated with a {@link Menu}.
	 * This method ensures that clicks within the menu's inventory are canceled,
	 * and delegates further handling to the menu's implementation of {@code onInventoryClick}.
	 * Additionally, it triggers any registered item-specific click event handlers.
	 *
	 * @param e The {@link InventoryClickEvent} representing the inventory interaction
	 *          triggered by the player. Contains information about the clicked
	 *          inventory, the clicked item, and other event details.
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory().getHolder() instanceof Menu menu) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null) {
				return;
			}
			
			menu.onInventoryClick(e);
			
			try {
				itemClickEvents.forEach((menu1, itemStackConsumerMap) -> {
					if (menu1.equals(menu)) {
						itemStackConsumerMap.forEach((itemStack, inventoryClickEventConsumer) -> {
							if (itemStack.equals(e.getCurrentItem())) {
								inventoryClickEventConsumer.accept(e);
							}
						});
					}
				});
			} catch (Exception ignore) {
			
			}
		}
	}
}
