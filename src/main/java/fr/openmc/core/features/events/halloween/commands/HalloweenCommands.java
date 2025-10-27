package fr.openmc.core.features.events.halloween.commands;

import fr.openmc.core.features.events.halloween.managers.HalloweenManager;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("halloween")
@CommandPermission("omc.admins.commands.halloween")
public class HalloweenCommands {
    @Subcommand("end")
    public void endHalloweenCommand() {
        HalloweenManager.endEvent();
    }
}
