package fr.openmc.core.features.city.commands.autocomplete;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class CityNameAutoComplete implements SuggestionProvider<BukkitCommandActor> {
    private final CityManager cityManager = OMCRegistry.FEATURES.CITY.get();

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return cityManager.getCitiesByName().keySet().stream().toList();
    }
}
