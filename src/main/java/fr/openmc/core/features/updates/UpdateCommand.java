package fr.openmc.core.features.updates;

import org.bukkit.entity.Player;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class UpdateCommand {
    @Command("omc version")
    @CommandPermission("omc.commands.version")
    @Description("Faire un lancé de dés (Donne un nombre aléatoire entre 1 et 10)")
    private void version(Player player) {
        UpdateManager.getInstance().sendUpdateMessage(player);
    }
}
