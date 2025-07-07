package fr.openmc.core.items.items;

import fr.openmc.core.items.CustomItem;
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
}
