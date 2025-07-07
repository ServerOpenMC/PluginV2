package fr.openmc.core.items.items.homes.icons;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Shop extends CustomItem {
    public Shop() {
        super("omc_homes:omc_homes_icon_shop");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.CHEST);
    }
}
