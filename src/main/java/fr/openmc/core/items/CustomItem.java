package fr.openmc.core.items;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.utils.api.ItemsAdderApi;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public abstract class CustomItem {
    public abstract ItemStack getVanilla();
    @Getter private final String name;

    public CustomItem(String name) {
        this.name = name;
        CustomItemRegistry.register(name, this);
    }

    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance(getName());
        return stack != null ? stack.getItemStack() : null;
    }

    @Override
    public boolean equals(Object anotherObject) {
        if (anotherObject instanceof ItemStack anotherItem) {
            CustomItem citem = CustomItemRegistry.getByItemStack(anotherItem);

            if (citem == null) return false;
            return citem.getName().equals(this.getName());
        }

        if (anotherObject instanceof String name) {
            return this.getName().equals(name);
        }

        if (anotherObject instanceof CustomItem citem) {
            return citem.getName().equals(this.getName());
        }

        return false;
    }

    /**
     * Order:
     * 1. ItemsAdder
     * 2. Vanilla
     * @return Best ItemStack to use for the server
     */
    public ItemStack getBest() {
        return getItemsAdder() == null ? getVanilla() : getItemsAdder();
    }
}