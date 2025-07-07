package fr.openmc.core.items.items;

import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MascotWand extends CustomItem {
    public MascotWand() {
        super("omc_items:mascot_stick");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.STICK);
    }
}
