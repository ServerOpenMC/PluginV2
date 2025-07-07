package fr.openmc.core.items.items.company;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.items.CustomItem;
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
}
