package fr.openmc.core.registry.items;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.utils.bukkit.ItemUtils;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public abstract class CustomItem {
    @Getter
    private final String id;

    /**
     * -- GETTER --
     *  Méthode à override afin d'ajouter des metas personnalisées
     */
    @Getter
    private CustomItemMeta meta;

    public CustomItem(String id) {
        this.id = id;
        this.meta = null;
    }

    public CustomItem(CustomItemMeta meta) {
        this.meta = meta;
        this.id = meta.getId();
    }

    public abstract ItemStack getVanilla();

    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance(getId());
        return stack != null ? stack.getItemStack() : null;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ItemStack anotherItem) {
            CustomItem citem = CustomItemRegistry.getByItemStack(anotherItem);

            if (citem == null) return false;
            return citem.getId().equals(this.getId());
        }

        if (object instanceof String otherObjectName) {
            return this.getId().equals(otherObjectName);
        }

        if (object instanceof CustomItem citem) {
            return citem.getId().equals(this.getId());
        }

        return false;
    }

    /**
     * Order:
     * 1. ItemsAdder
     * 2. Vanilla
     *
     * @return Best ItemStack to use for the server
     */
    public ItemStack getBest() {
        ItemStack item;
        if (!ItemsAdderHook.isEnable() || getItemsAdder() == null) {
            item = getVanilla();
        } else {
            item = getItemsAdder();
        }

        ItemUtils.setTag(item, CustomItemRegistry.CUSTOM_ITEM_KEY, this.getId());

        return item;
    }
}