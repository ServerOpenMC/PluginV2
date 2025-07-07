package fr.openmc.core.items.buttons;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class OneButton extends CustomItem {

    public OneButton() {
        super("omc_menus:1_btn");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.PAPER);
    }
}
