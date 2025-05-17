package fr.openmc.core.utils.customitems.items;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.utils.customitems.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class KebabItem extends CustomItem {

    public KebabItem() {
        super("omc_foods:kebab");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.COOKED_BEEF);
    }


    @Override
    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance(this.getName());
        if (stack != null) return stack.getItemStack();
        else return null;
    }
}
