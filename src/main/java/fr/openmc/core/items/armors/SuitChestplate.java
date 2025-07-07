package fr.openmc.core.items.armors;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.items.CustomItem;
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
}
