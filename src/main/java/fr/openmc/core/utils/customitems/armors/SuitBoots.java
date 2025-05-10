package fr.openmc.core.utils.customitems.armors;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.utils.customitems.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SuitBoots extends CustomItem {
    public SuitBoots() {
        super("omc_items:suit_boots");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.IRON_BOOTS);
    }

    @Override
    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance("omc_items:suit_boots");
        if (stack != null) {
            return stack.getItemStack();
        } else {
            return null;
        }
    }
}
