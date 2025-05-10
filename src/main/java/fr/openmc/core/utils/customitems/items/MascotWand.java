package fr.openmc.core.utils.customitems.items;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.utils.customitems.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MascotWand extends CustomItem {
    public MascotWand() {
        super("omc_items:mascot_stick");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.STICK);
    }

    @Override
    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance("omc_items:mascot_stick");
        if (stack != null) {
            return stack.getItemStack();
        } else {
            return null;
        }
    }
}
