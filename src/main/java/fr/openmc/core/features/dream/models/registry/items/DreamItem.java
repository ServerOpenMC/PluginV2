package fr.openmc.core.features.dream.models.registry.items;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.hooks.ItemsAdderHook;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public abstract class DreamItem {
    public abstract ItemStack getVanilla();

    private final String name;

    /**
     * Creates a new DreamItem with the specified name.
     *
     * @param name The namespaced ID of the item, e.g., "omc_dream:orb".
     */
    protected DreamItem(String name) {
        this.name = name;
    }

    public abstract DreamRarity getRarity();

    public abstract boolean isTransferable();

    public abstract ItemStack getTransferableItem();

    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance(getName());
        return stack != null ? stack.getItemStack() : null;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ItemStack anotherItem) {
            DreamItem citem = DreamItemRegistry.getByItemStack(anotherItem);

            if (citem == null) return false;
            return citem.getName().equals(this.getName());
        }

        if (object instanceof String otherObjectName) {
            return this.getName().equals(otherObjectName);
        }

        if (object instanceof DreamItem citem) {
            return citem.getName().equals(this.getName());
        }

        return false;
    }

    private List<Component> getGeneratedLore() {
        ItemStack baseItem;

        if (!ItemsAdderHook.isHasItemAdder() || getItemsAdder() == null) {
            baseItem = getVanilla();
        } else {
            baseItem = getItemsAdder();
        }

        List<Component> lore = baseItem.lore();
        if (lore == null) lore = new ArrayList<>();

        if (this instanceof DreamEquipableItem equipableItem) {
            lore.add(Component.empty());

            lore.add(Component.text("§7§oTemps maximum: §r§a+" + equipableItem.getAdditionalMaxTime() + "s"));

            Integer coldResistance = equipableItem.getColdResistance();
            if (coldResistance != null) {
                lore.add(Component.text("§7§oResistance au froid: §r§b+" + coldResistance));
            }
        }

        lore.add(Component.empty());

        if (isTransferable()) {
            lore.add(Component.text("§9§ko §r§9Dream Transferable §9§ko"));
        }

        lore.add(this.getRarity().getTemplateLore());

        return lore;
    }

    private List<Component> getGeneratedLoreTransferable() {
        ItemStack baseItem;

        if (!ItemsAdderHook.isHasItemAdder() || getItemsAdder() == null) {
            baseItem = getVanilla();
        } else {
            baseItem = getItemsAdder();
        }

        List<Component> lore = baseItem.lore();
        if (lore == null) lore = new ArrayList<>();

        lore.add(Component.empty());

        if (isTransferable()) {
            lore.add(Component.text("§9§ko §r§9Dream Transferable §9§ko"));
        }

        lore.add(this.getRarity().getTemplateLore());

        return lore;
    }

    /**
     * Order:
     * 1. ItemsAdder
     * 2. Vanilla
     *
     * @return Best ItemStack to use for the server
     */
    public ItemStack getBestTransferable() {
        ItemStack item;
        if (!ItemsAdderHook.isHasItemAdder() || getItemsAdder() == null) {
            item = getVanilla();
        } else {
            item = getItemsAdder();
        }

        ItemUtils.setTag(item, DreamItemRegistry.CUSTOM_NAME_KEY, this.getName());
        item.lore(this.getGeneratedLoreTransferable());

        return item;
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
        if (!ItemsAdderHook.isHasItemAdder() || getItemsAdder() == null) {
            item = getVanilla();
        } else {
            item = getItemsAdder();
        }

        ItemUtils.setTag(item, DreamItemRegistry.CUSTOM_NAME_KEY, this.getName());
        item.lore(this.getGeneratedLore());

        return item;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
