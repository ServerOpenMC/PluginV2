package fr.openmc.core.features.adminshop.menu.category.defaults;

import fr.openmc.core.features.adminshop.AdminShopCategory;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseShop;
import org.bukkit.entity.Player;

public class AdminShopDefaults extends BaseShop {
    public AdminShopDefaults(Player player, AdminShopCategory category) {
        super(player, "§6Admin Shop - " + category.getName(), category.getItems());
    }
}
