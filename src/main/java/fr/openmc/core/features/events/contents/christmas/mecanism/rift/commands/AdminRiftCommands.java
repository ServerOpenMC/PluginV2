package fr.openmc.core.features.events.contents.christmas.mecanism.rift.commands;

import fr.openmc.core.features.events.contents.christmas.mecanism.rift.RiftManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("christmas admin")
@CommandPermission("omc.admins.commands.christmas.admin")
public class AdminRiftCommands {
    @Subcommand("placeRiftOrigin")
    @CommandPermission("omc.admins.commands.christmas.admin.placeriftorigin")
    void placeRiftOrigin(
            Player player
    ) {
        Location origin = new Location(player.getWorld(), 0, 100, 0);
        RiftManager.placeRift(origin);
    }
}
