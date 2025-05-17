package fr.openmc.core.utils.customitems.items;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.utils.customitems.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ContestShell extends CustomItem {
    public ContestShell() {
        super("omc_contest:contest_shell");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.NAUTILUS_SHELL);
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
