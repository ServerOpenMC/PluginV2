package fr.openmc.api.menulib;

import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an abstract Menu framework for managing custom player inventories.
 * A menu is tied to a specific player and provides methods for customization,
 * handling inventory interactions, and managing permissions.
 */
public abstract class Menu implements InventoryHolder {
	
	@Getter
	private final Player owner;
	
	/**
	 * Constructs a new Menu for the specified player.
	 *
	 * @param owner The {@link Player} who owns the menu
	 */
	public Menu(Player owner) {
		this.owner = owner;
	}
	
	
	/**
	 * Retrieves the name of the menu.
	 *
	 * @return A non-null {@link String} representing the name of the menu
	 */
	@NotNull
	public abstract String getName();
	
	/**
	 * Retrieves the size of the inventory for the menu.
	 *
	 * @return An {@link InventorySize} constant representing the size of the inventory.
	 */
	@NotNull
	public abstract InventorySize getInventorySize();
	
	public String getPermission() {
		return null;
	}
	
	public String getNoPermissionMessage() {
		return "";
	}
	
	/**
	 * Handles the event that occurs when a player interacts with the menu's inventory.
	 * This method is called whenever an {@link InventoryClickEvent} is triggered for a menu
	 * controlled by this class. Subclasses should implement the logic to respond
	 * to clicks in the inventory, such as handling button functionality or item interactions.
	 *
	 * @param e The {@link InventoryClickEvent} containing details about the click action,
	 *          including the player who clicked, the clicked inventory slot, and other relevant event data.
	 */
	public abstract void onInventoryClick(InventoryClickEvent e);
	
	/**
	 * Retrieves the content of this menu as a mapping between inventory slot indexes and {@link ItemStack}s.
	 * Each entry in the map represents an item stored in a specific slot of the menu's inventory.
	 *
	 * @return A non-null {@link Map} where the key is an integer representing an inventory slot index,
	 * and the value is the {@link ItemStack} present in that slot.
	 */
	@NotNull
	public abstract Map<Integer, ItemStack> getContent();
	
	/**
	 * Opens the menu for the owner player. If the menu specifies a required permission,
	 * the method checks if the owner has the necessary permission. If not, a "no permission"
	 * message is sent to the owner and the menu does not open.
	 * <p>
	 * The inventory for the menu is created using {@link #getInventory()} and populated
	 * with items from {@link #getContent()}. The populated inventory is then opened for
	 * the owner player.
	 */
	public final void open() {
		if (getPermission() != null && ! getPermission().isEmpty()) {
			if (! owner.hasPermission(getPermission())) {
				owner.sendMessage(getNoPermissionMessage());
				return;
			}
		}
		Inventory inventory = getInventory();
		getContent().forEach(inventory::setItem);
		owner.openInventory(inventory);
	}
	
	/**
	 * Fills the entire inventory with items made from the specified material.
	 * Each slot in the inventory is populated with an {@link ItemStack} that uses the
	 * provided material and has a blank name.
	 *
	 * @param material The {@link Material} to use for creating {@link ItemStack}s to fill the inventory.
	 * @return A {@link Map} where the key represents the inventory slot index, and the value is the {@link ItemStack} placed
	 * in that slot.
	 */
	public final Map<Integer, ItemStack> fill(Material material) {
		Map<Integer, ItemStack> map = new HashMap<>();
		for (int i = 0; i < getInventorySize().getSize(); i++) {
			ItemStack filler = ItemUtils.createItem(" ", material);
			map.put(i, filler);
		}
		return map;
	}
	
	/**
	 * Checks if the given {@link ItemStack} is associated with the specified item ID.
	 *
	 * @param item   The {@link ItemStack} to be checked
	 * @param itemId The ID of the item to compare against
	 * @return {@code true} if the item matches the specified ID, otherwise {@code false}
	 */
	public final boolean isItem(ItemStack item, String itemId) {
		PersistentDataContainer dataContainer = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
		if (dataContainer.has(MenuLib.getItemIdKey(), PersistentDataType.STRING)) {
			return Objects.equals(dataContainer.get(MenuLib.getItemIdKey(), PersistentDataType.STRING), itemId);
		}
		return false;
	}
	
	/**
	 * Opens the previously viewed menu for the owner of the current menu.
	 * This method retrieves the last menu accessed by the owner using {@link MenuLib#getLastMenu(Player)}
	 * and opens it by calling its {@code open} method.
	 */
	public final void back() {
		Menu lastMenu = MenuLib.getLastMenu(owner);
		lastMenu.open();
	}
	
	/**
	 * Creates and returns the inventory associated with this menu.
	 * The inventory is created with the size specified by {@link #getInventorySize()}
	 * and named using {@link #getName()}. The menu itself is set as the inventory holder.
	 *
	 * @return The inventory object for this menu
	 */
	@NotNull
	@Override
	public final Inventory getInventory() {
		return Bukkit.createInventory(this, getInventorySize().getSize(), Component.text(getName()));
	}
}
