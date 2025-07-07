package fr.openmc.core.items.items.homes.icons;

import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Axenq extends CustomItem {
    public Axenq() {
        super("omc_homes:omc_homes_icon_axenq");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.CHEST);
    }
}
