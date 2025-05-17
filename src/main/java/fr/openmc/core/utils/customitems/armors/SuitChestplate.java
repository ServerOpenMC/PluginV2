package fr.openmc.core.utils.customitems.armors;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.utils.customitems.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SuitChestplate extends CustomItem {
    public SuitChestplate() {
        super("omc_items:suit_chestplate");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.IRON_CHESTPLATE);
    }

    @Override
    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance("omc_items:suit_chestplate");
        if (stack != null) {
            return stack.getItemStack();
        } else {
            return null;
        }
    }
}
