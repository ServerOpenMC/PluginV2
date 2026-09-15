package fr.openmc.core.features.adminshop;

import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;


public class AdminShopCommand {
    private final AdminShopManager manager;

    public AdminShopCommand(AdminShopManager manager) {
        this.manager = manager;
    }

    @Command("adminshop")
    @Description("Ouvrir le menu du shop admin")
    @CommandPermission("omc.commands.adminshop")
    public void openAdminShop(Player player) {
        manager.openMainMenu(player);
    }
}