package fr.openmc.core.features.dream.commands.autocomplete;

import fr.openmc.core.features.dream.milestone.DreamSteps;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Arrays;
import java.util.Collection;

public class DreamMilestoneStepsAutoComplete implements SuggestionProvider<BukkitCommandActor> {
	@Override
	public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
		return Arrays.stream(DreamSteps.values()).map(Enum::name).toList();
	}
}
