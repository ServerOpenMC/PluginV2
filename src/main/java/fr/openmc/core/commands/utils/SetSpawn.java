package fr.openmc.core.commands.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class SetSpawn {
    private final MessagesManager msgOMC  = new MessagesManager(Prefix.OPENMC);

    @Command("setspawn")
    @Description("Permet de changer le spawn")
    @CommandPermission("omc.admin.commands.setspawn")
    public void setSpawn(Player player) {

        Location location = player.getLocation();

        SpawnManager.getInstance().setSpawn(location);

        msgOMC.success(player, "§aVous avez changé le point de spawn en §6X: §e" + location.getBlockX() + "§6, Y:§e" + location.getBlockY() + "§6, Z: §e" + location.getBlockY());

    }
}
