package fr.openmc.core.features.settings.command;

import fr.openmc.core.features.settings.menu.PlayerSettingsMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class SettingsCommand {

    @Command("settings")
    @Description("Affiche les paramètres de votre compte")
    @CommandPermission("omc.commands.settings")
    public static void settings(Player player) {
        new PlayerSettingsMenu(player).open();
    }

}