package fr.openmc.core.items.armors;

import fr.openmc.core.items.CustomItem;
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
}
