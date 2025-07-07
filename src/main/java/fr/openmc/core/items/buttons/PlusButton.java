package fr.openmc.core.items.buttons;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PlusButton extends CustomItem {

    public PlusButton() {
        super("omc_menus:plus_btn");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.PAPER);
    }
}
