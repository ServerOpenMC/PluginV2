package fr.openmc.core.features.millestones;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Set;

@Command("milestone")
@CommandPermission("omc.commands.milestone")
public class MillestoneCommand {

    private final Set<Millestone> millestones;

    public MillestoneCommand(Set<Millestone> millestones) {
        this.millestones = millestones;
    }
}
