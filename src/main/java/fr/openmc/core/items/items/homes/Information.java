package fr.openmc.core.items.items.homes;

import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Information extends CustomItem {
    public Information() {
        super("omc_homes:omc_homes_icon_information");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.CHEST);
    }
}
