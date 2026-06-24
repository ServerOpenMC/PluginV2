package fr.openmc.core.registry.mobs.commands;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.mobs.CustomMobEntry;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class CustomMobsAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return OMCRegistry.CUSTOM_MOBS.values()
                .stream()
                .map(CustomMobEntry::id)
                .map(id -> id.split(":")[1])
                .toList();
    }
}
