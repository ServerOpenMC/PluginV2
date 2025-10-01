package fr.openmc.core.items;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.hooks.ItemsAdderHook;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public abstract class CustomItem {
    public abstract ItemStack getVanilla();
    @Getter private final String name;

    public CustomItem(String name) {
        this.name = name;
    }

    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance(getName());
        return stack != null ? stack.getItemStack() : null;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ItemStack anotherItem) {
            CustomItem citem = CustomItemRegistry.getByItemStack(anotherItem);

            if (citem == null) return false;
            return citem.getName().equals(this.getName());
        }

        if (object instanceof String otherObjectName) {
            return this.getName().equals(otherObjectName);
        }

        if (object instanceof CustomItem citem) {
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
        return !ItemsAdderHook.isHasItemAdder() || getItemsAdder() == null ? getVanilla() : getItemsAdder();
    }
}