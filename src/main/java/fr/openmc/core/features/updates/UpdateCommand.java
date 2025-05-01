package fr.openmc.core.features.updates;

import org.bukkit.entity.Player;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class UpdateCommand {

    @Command("omc version")
    @CommandPermission("omc.commands.version")
    @Description("Vous donne de l'information sur le version du plugin")
    private void version(Player player) {
        UpdateManager.getInstance().sendUpdateMessage(player);
    }
}
