package fr.openmc.core.items.buttons;

import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StackButton extends CustomItem {

    public StackButton() { super("omc_menus:64_btn"); }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.PAPER);
    }
}
