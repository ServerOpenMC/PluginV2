package fr.openmc.core.utils.customitems;

import dev.lone.itemsadder.api.CustomStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Aywenite extends CustomItem {
    public Aywenite() {
        super("omc_items:aywenite");
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        return item;
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
