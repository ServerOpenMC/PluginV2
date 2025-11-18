package fr.openmc.core.features.city.commands.autocomplete;

import fr.openmc.core.features.city.CityPermission;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CityPermissionsAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return Arrays.stream(CityPermission.values())
                .map(CityPermission::name)
                .collect(Collectors.toList());
    }
}
