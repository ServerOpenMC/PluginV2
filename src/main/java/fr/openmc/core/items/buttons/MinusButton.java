package fr.openmc.core.items.buttons;

import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MinusButton extends CustomItem {

    public MinusButton() {
        super("omc_menus:minus_btn");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.PAPER);
    }
}
