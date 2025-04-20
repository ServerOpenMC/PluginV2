package fr.openmc.core.features.adminshop;

import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;


public class AdminShopCommand {

    @Command("adminshop")
    @Description("Ouvrir le menu du shop admin")
    public void openAdminShop(Player player) {
        AdminShopManager.getInstance().openMainMenu(player);
    }

}
