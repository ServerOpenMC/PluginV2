package fr.openmc.core.items.items;

import fr.openmc.core.items.CustomItem;
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
}
