package fr.openmc.core.items.items.homes.icons;

import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Farm extends CustomItem {
    public Farm() {
        super("omc_homes:omc_homes_icon_zombie");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.CHEST);
    }
}
