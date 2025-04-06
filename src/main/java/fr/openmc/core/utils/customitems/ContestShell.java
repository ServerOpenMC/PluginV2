package fr.openmc.core.utils.customitems;

import dev.lone.itemsadder.api.CustomStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ContestShell extends CustomItem {
    public ContestShell() {
        super("omc_contest:contest_shell");
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.NAUTILUS_SHELL);
        return item;
    }

    @Override
    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance("omc_contest:contest_shell");
        if (stack != null) {
            return stack.getItemStack();
        } else {
            return null;
        }
    }
}
