package fr.openmc.core.utils.customitems.items.homes.icons;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.utils.customitems.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Sandblock extends CustomItem {
    public Sandblock() {
        super("omc_homes:omc_homes_icon_sandblock");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.CHEST);
    }

    @Override
    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance("omc_homes:omc_homes_icon_sandblock");
        if (stack != null) {
            return stack.getItemStack();
        } else {
            return null;
        }
    }
}
