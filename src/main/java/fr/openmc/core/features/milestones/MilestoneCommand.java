package fr.openmc.core.features.milestones;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Set;

@Command("milestone")
@CommandPermission("omc.commands.milestone")
public class MilestoneCommand {

    private final Set<Milestone> milestones;

    public MilestoneCommand(Set<Milestone> milestones) {
        this.milestones = milestones;
    }
}
