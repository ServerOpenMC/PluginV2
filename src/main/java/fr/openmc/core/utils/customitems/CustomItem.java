package fr.openmc.core.utils.customitems;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public abstract class CustomItem {
    public abstract ItemStack getVanilla();
    public abstract ItemStack getItemsAdder();
    @Getter private final String name;

    public CustomItem(String name) {
        this.name = name;
        CustomItemRegistry.register(name, this);
    }

    /**
     * Order:
     * 1. ItemsAdder
     * 2. Vanilla
     * @return Best ItemStack to use for the server
     */
    public ItemStack getBest() {
        ItemStack item = getItemsAdder();

        if (item == null) {
            item = getVanilla();
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(CustomItemRegistry.customNameKey, PersistentDataType.STRING, name);
        item.setItemMeta(meta);

        return item;
    }
}