package fr.openmc.core.features.events.halloween.commands;

import fr.openmc.core.features.events.halloween.managers.HalloweenManager;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;

@Command("halloween")
public class HalloweenCommands {
    @Subcommand("end")
    public void endHalloweenCommand() {
        HalloweenManager.endEvent();
    }
}
