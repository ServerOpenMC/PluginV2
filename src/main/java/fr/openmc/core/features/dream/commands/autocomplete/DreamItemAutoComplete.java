package fr.openmc.core.features.dream.commands.autocomplete;

import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.items.CustomItemRegistry;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class DreamItemAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return CustomItemRegistry.getNames()
                .stream()
                .filter(name -> name.startsWith("omc_dream:"))
                .map(name -> name.replace("omc_dream:", ""))
                .toList();
    }
}
