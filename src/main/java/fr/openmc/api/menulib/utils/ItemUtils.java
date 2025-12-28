package fr.openmc.api.menulib.utils;

import fr.openmc.api.menulib.MenuLib;
import fr.openmc.core.utils.cache.CachePlayerProfile;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

/**
 * Utility class for performing operations on {@link ItemStack}.
 * Provides methods for creating items, checking item identifiers, and creating player skulls.
 */
public class ItemUtils {
	
	/**
	 * Creates an {@link ItemStack} with the specified display name and material.
	 *
	 * @param name     the display name of the item
	 * @param material the material type of the item
	 * @return the created ItemStack with the given name and material
	 */
	public static ItemStack createItem(String name, Material material) {
		ItemStack itemStack = new ItemStack(material);
		ItemMeta meta = itemStack.getItemMeta();
		if (meta != null) {
			meta.displayName(Component.text(name));
		}
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	/**
	 * Checks if the provided {@link ItemStack} has the specified item ID stored in its
	 * {@link PersistentDataContainer}.
	 *
	 * @param item   The {@link ItemStack} to be checked for the specified item ID.
	 * @param itemId The item ID to be compared against the value stored in the persistent data container.
	 * @return {@code true} if the item has a matching item ID; {@code false} otherwise.
	 */
	public static boolean isItem(ItemStack item, String itemId) {
		PersistentDataContainer dataContainer = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
		if (dataContainer.has(MenuLib.getItemIdKey(), PersistentDataType.STRING)) {
			return Objects.equals(dataContainer.get(MenuLib.getItemIdKey(), PersistentDataType.STRING), itemId);
		}
		return false;
	}

	/**
	 * Get an array of DataComponentType that are allowed for items.
	 *
	 * @return An array of DataComponentType.
	 */
	public static DataComponentType[] getDataComponentType() {
		return new DataComponentType[] {
				DataComponentTypes.CONSUMABLE,
				DataComponentTypes.FOOD,
				DataComponentTypes.BUNDLE_CONTENTS,
				DataComponentTypes.ENCHANTMENTS,
				DataComponentTypes.DAMAGE,
				DataComponentTypes.DAMAGE_RESISTANT,
				DataComponentTypes.UNBREAKABLE,
				DataComponentTypes.ATTRIBUTE_MODIFIERS,
				DataComponentTypes.TRIM,
				DataComponentTypes.PROVIDES_TRIM_MATERIAL,
				DataComponentTypes.JUKEBOX_PLAYABLE,
				DataComponentTypes.FIREWORKS,
				DataComponentTypes.FIREWORK_EXPLOSION,
				DataComponentTypes.POTION_CONTENTS,
				DataComponentTypes.POTION_DURATION_SCALE,
				DataComponentTypes.DEATH_PROTECTION,
				DataComponentTypes.DYED_COLOR,
				DataComponentTypes.CONTAINER_LOOT,
				DataComponentTypes.CONTAINER,
				DataComponentTypes.RARITY
		};
	}
}