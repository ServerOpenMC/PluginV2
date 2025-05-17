package fr.openmc.core.utils.customitems.armors;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.utils.customitems.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SuitLeggings extends CustomItem {
    public SuitLeggings() {
        super("omc_items:suit_leggings");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.IRON_LEGGINGS);
    }

    @Override
    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance("omc_items:suit_leggings");
        if (stack != null) {
            return stack.getItemStack();
        } else {
            return null;
        }
    }
}
