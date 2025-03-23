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

    private ItemStack format(ItemStack initial) {
        ItemMeta meta = initial.getItemMeta();
        meta.displayName(Component.text("Aywenite")
                .decoration(TextDecoration.ITALIC, false)
                .color(NamedTextColor.LIGHT_PURPLE)
        );
        initial.setItemMeta(meta);
        return initial;
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        return format(item);
    }

    @Override
    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance("omc_items:aywenite");
        if (stack != null) {
            return format(stack.getItemStack());
        } else {
            return null;
        }
    }
}
