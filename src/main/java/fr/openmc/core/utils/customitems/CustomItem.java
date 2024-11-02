package fr.openmc.core.utils.customitems;

import org.bukkit.inventory.ItemStack;

public abstract class CustomItem {
    public abstract ItemStack getVanilla();
    public abstract ItemStack getItemsAdder();

    /**
     * Order:
     * 1. ItemsAdder
     * 2. Vanilla
     * @return Best ItemStack to use for the server
     */
    public ItemStack getBest() {
        ItemStack itemsAdder = getItemsAdder();
        return itemsAdder != null ? itemsAdder : getVanilla();
    }
}