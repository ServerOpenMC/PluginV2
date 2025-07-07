package fr.openmc.core.items.items.homes.icons;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Home extends CustomItem {
    public Home() {
        super("omc_homes:omc_homes_icon_maison");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.CHEST);
    }
}
