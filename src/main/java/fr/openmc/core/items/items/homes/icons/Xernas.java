package fr.openmc.core.items.items.homes.icons;

import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Xernas extends CustomItem {
    public Xernas() {
        super("omc_homes:omc_homes_icon_xernas");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.CHEST);
    }
}
