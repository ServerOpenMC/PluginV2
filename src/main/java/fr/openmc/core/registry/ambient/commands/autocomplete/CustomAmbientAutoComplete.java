package fr.openmc.core.registry.ambient.commands.autocomplete;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.ambient.CustomAmbient;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class CustomAmbientAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return OMCRegistry.CUSTOM_AMBIENTS.values()
                .stream()
                .map(CustomAmbient::getId)
                .toList();
    }
}