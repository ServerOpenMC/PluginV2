package fr.openmc.core.utils.customitems.items;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.utils.customitems.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Aywenite extends CustomItem {
    public Aywenite() {
        super("omc_items:aywenite");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.AMETHYST_SHARD);
    }

    @Override
    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance("omc_items:aywenite");
        if (stack != null) {
            return stack.getItemStack();
        } else {
            return null;
        }
    }
}
