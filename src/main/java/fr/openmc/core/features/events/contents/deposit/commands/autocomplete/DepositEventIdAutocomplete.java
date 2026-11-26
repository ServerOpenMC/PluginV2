package fr.openmc.core.features.events.contents.deposit.commands.autocomplete;

import fr.openmc.core.features.events.contents.deposit.DepositEventManager;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class DepositEventIdAutocomplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return DepositEventManager.getEventIds().stream()
                .toList();
    }
}
