package fr.openmc.core.features.adminshop;

import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;


public class AdminShopCommand {
    @Command("adminshop")
    @Description("Ouvrir le menu du shop admin")
    @CommandPermission("omc.commands.adminshop")
    public void openAdminShop(Player player) {
        AdminShopManager.openMainMenu(player);
    }
}