package fr.openmc.core.commands.adminshop;

import fr.openmc.core.features.adminshop.menu.AdminShopMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;

public class AdminShopCommand {
    @Command({"adminshop", "adm"})
    public void openAdminShop(Player owner) {
        new AdminShopMenu(owner).open();
    }
}
