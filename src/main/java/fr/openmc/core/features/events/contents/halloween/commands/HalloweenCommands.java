package fr.openmc.core.features.events.contents.halloween.commands;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.managers.HalloweenManager;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("halloween")
@CommandPermission("omc.admins.commands.halloween")
public class HalloweenCommands {
    private final HalloweenManager halloweenManager = OMCRegistry.FEATURES.HALLOWEEN.get();
    @Subcommand("end")
    public void endHalloweenCommand() {
        halloweenManager.endEvent();
    }
}
