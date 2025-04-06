package fr.openmc.core.utils.customitems;

import dev.lone.itemsadder.api.CustomStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AcceptButton extends CustomItem {
    public AcceptButton() {
        super("omc_menus:accept_btn");
    }

    private ItemStack format(ItemStack initial) {
        ItemMeta meta = initial.getItemMeta();
        meta.displayName(Component.text("Accepter").decoration(TextDecoration.ITALIC, false));
        initial.setItemMeta(meta);
        return initial;
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.GREEN_CONCRETE);
        return format(item);
    }

    @Override
    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance("omc_menus:accept_btn");
        if (stack != null) {
            return format(stack.getItemStack());
        } else {
            return null;
        }
    }
}
