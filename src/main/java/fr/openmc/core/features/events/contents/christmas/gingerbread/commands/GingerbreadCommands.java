package fr.openmc.core.features.events.contents.christmas.gingerbread.commands;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.events.contents.christmas.gingerbread.managers.GingerBreadManager;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("gingerbread")
@CommandPermission("omc.admins.commands.gingerbread")
public class GingerbreadCommands {
    @Subcommand("admin")
    public static class GingerbreadAdminCommands {
        @Subcommand("placenpc")
        public void placeNpcCommand(OMCPlayer player) {
            
        }
    }
    @Subcommand("endGingerbread")
    public void endHalloweenCommand() {
        GingerBreadManager.endEvent();
    }
}
