package fr.openmc.core.utils.customitems;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;

public class CustomItemRegistry {
    static HashMap<String, CustomItem> items = new HashMap<>();
    static NamespacedKey customNameKey = new NamespacedKey("aywen", "custom_item");

    static public void init() {
        // Ici, enregistrer tout les items custom
        new CloseButton();
        new PreviousPage();
        new NextPage();
    }

    public static void register(String name, CustomItem item) {
        if (items.containsKey(name)) {
            throw new IllegalArgumentException("Custom item with name " + name + " already exists");
        }

        if (!name.matches("[a-zA-Z0-9_:]+")) {
            throw new IllegalArgumentException("Custom item name dont match regex \"[a-zA-Z0-9_:]+\"");
        }

        items.put(name, item);
    }

    @Nullable
    public static CustomItem getByName(String name) {
        return items.get(name);
    }

    @Nullable
    public static CustomItem getByItemStack(ItemStack stack) {
        PersistentDataContainerView view = stack.getPersistentDataContainer();
        String name = view.get(customNameKey, PersistentDataType.STRING);

        if (name == null) return null;
        return getByName(name);
    }

    public static HashSet<String> getNames() {
        return new HashSet<>(items.keySet());
    }
}
