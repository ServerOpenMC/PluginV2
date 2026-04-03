package fr.openmc.core.features.events.contents.weeklyevents.contents.contest.commands.autocomplete;

import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.ContestPhase;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Arrays;
import java.util.List;

public class ContestPhaseAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return Arrays.stream(ContestPhase.values())
                .map(Enum::name)
                .toList();
    }
}
