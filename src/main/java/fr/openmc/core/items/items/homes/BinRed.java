package fr.openmc.core.items.items.homes;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BinRed extends CustomItem {
    public BinRed() {
        super("omc_homes:omc_homes_icon_bin_red");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.CHEST);
    }
}
