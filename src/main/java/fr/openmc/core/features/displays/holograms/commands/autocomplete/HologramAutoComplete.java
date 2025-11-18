package fr.openmc.core.features.displays.holograms.commands.autocomplete;

import fr.openmc.core.features.displays.holograms.HologramLoader;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class HologramAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return HologramLoader.displays.keySet().stream().toList();
    }
}
