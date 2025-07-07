package fr.openmc.core.items.items.homes.icons;

import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Default extends CustomItem {
    public Default() {
        super("omc_homes:omc_homes_icon_grass");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.CHEST);
    }
}
