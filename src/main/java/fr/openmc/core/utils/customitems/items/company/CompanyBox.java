package fr.openmc.core.utils.customitems.items.company;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.utils.customitems.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CompanyBox extends CustomItem {
    public CompanyBox() {
        super("omc_company:company_box");
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.CHEST);
    }

    @Override
    public ItemStack getItemsAdder() {
        CustomStack stack = CustomStack.getInstance("omc_company:company_box");
        if (stack != null) {
            return stack.getItemStack();
        } else {
            return null;
        }
    }
}
