package fr.openmc.core.items.armors;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SuitHelmet extends CustomItem {
    public SuitHelmet() {
        super("omc_items:suit_helmet");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.IRON_HELMET);
    }
}
