package fr.openmc.core.items.items;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.items.CustomItem;
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
}
