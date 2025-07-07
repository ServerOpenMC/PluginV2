package fr.openmc.core.items.armors;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.items.CustomItem;
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


}
