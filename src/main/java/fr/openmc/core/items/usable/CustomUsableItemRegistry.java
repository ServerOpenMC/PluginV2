package fr.openmc.core.items.usable;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CustomUsableItemRegistry {

    private static final Map<String, CustomUsableItem> items = new HashMap<>();

    public CustomUsableItemRegistry() {
        // register here
    }

    public static void register(CustomUsableItem item) {
        items.put(item.getName(), item);
    }

    public static CustomUsableItem getByName(String name) {
        return items.get(name);
    }

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

    public static boolean isUsableItem(ItemStack itemStack) {
        return getByItemStack(itemStack) != null;
    }

    public static Map<String, CustomUsableItem> getAllItems() {
        return new HashMap<>(items);
    }

}
