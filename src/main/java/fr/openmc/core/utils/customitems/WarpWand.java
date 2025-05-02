package fr.openmc.core.utils.customitems;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class WarpWand extends CustomItem {
    public WarpWand() {
        super("omc_items:warp_stick");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.STICK);
    }

    @Override
    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance("omc_items:warp_stick");
        if (stack != null) {
            return stack.getItemStack();
        } else {
            return null;
        }
    }
}