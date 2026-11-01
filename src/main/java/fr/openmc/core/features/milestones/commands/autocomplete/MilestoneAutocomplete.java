package fr.openmc.core.features.milestones.commands.autocomplete;


import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.milestones.models.Milestone;
import fr.openmc.core.features.milestones.models.MilestoneType;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class MilestoneAutocomplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return MilestonesManager.getRegisteredMilestones().stream()
                .map(Milestone::getType)
                .map(MilestoneType::name)
                .toList();
    }
}

