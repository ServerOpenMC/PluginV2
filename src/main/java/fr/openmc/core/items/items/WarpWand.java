package fr.openmc.core.items.items;

import fr.openmc.core.items.CustomItem;
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
}