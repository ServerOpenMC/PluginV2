package fr.openmc.core.features.milestones.commands.autocomplete;

import fr.openmc.core.features.milestones.commands.MilestoneCommand;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

/**
 * Suggestions pour {@code /milestones <milestone>}.
 * <p>
 * Contrairement à {@link MilestoneAutocomplete} (réservé aux commandes admin, qui attend les
 * noms exacts de {@code MilestoneType}), celle-ci propose des noms en minuscules et inclut le
 * milestone de ville.
 */
public class PlayerMilestoneAutocomplete implements SuggestionProvider<BukkitCommandActor> {
    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return MilestoneCommand.getMilestoneNames();
    }
}
