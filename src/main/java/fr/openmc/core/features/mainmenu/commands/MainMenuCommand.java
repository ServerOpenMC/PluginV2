package fr.openmc.core.features.mainmenu.commands;

import fr.openmc.core.features.mainmenu.MainMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class MainMenuCommand {
    @Command("menu")
    @Description("Permet d'ouvrir le menu principal d'OpenMC")
    @CommandPermission("omc.commands.menu")
    public void menuCommand(Player player) {
        MainMenu.openMainMenu(player);
    }
}
