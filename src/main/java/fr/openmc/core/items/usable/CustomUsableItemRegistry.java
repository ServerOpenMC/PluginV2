package fr.openmc.core.items.usable;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CustomUsableItemRegistry {

    private static final Map<String, CustomUsableItem> items = new HashMap<>();

    /**
     * Initializes the registry and registers all custom usable items.
     * This constructor should be called once during server startup.
     */
    public CustomUsableItemRegistry() {
        // register here
    }

    /**
     * Registers a custom usable item in the registry.
     *
     * @param item The CustomUsableItem to register.
     */
    public static void register(CustomUsableItem item) {
        items.put(item.getName(), item);
    }

    /**
     * Gets a custom usable item by its name.
     *
     * @param name The namespaced ID of the item, e.g., "omc_items:iron_hammer".
     * @return The {@link CustomUsableItem} associated with the given name, or null if not found.
     */
    public static CustomUsableItem getByName(String name) {
        return items.get(name);
    }

    /**
     * Gets a custom usable item by its ItemStack.
     *
     * @param itemStack The ItemStack to check.
     * @return The {@link CustomUsableItem} associated with the given ItemStack, or null if not found.
     */
    public static CustomUsableItem getByItemStack(ItemStack itemStack) {
        if (itemStack == null) return null;

        CustomStack customStack = CustomStack.byItemStack(itemStack);
        if (customStack != null) {
            String namespacedId = customStack.getNamespacedID();
            return items.get(namespacedId);
        }

        for (CustomUsableItem item : items.values()) {
            if (item.getVanilla().isSimilar(itemStack)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Checks if the given ItemStack is a usable item.
     *
     * @param itemStack The ItemStack to check.
     * @return true if the ItemStack is a usable item, false otherwise.
     */
    public static boolean isUsableItem(ItemStack itemStack) {
        return getByItemStack(itemStack) != null;
    }

    /**
     * Gets all registered custom usable items.
     *
     * @return A map of all custom usable items, where the key is the namespaced ID and the value is the CustomUsableItem.
     */
    public static Map<String, CustomUsableItem> getAllItems() {
        return new HashMap<>(items);
    }

}
