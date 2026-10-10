package fr.openmc.core.features.homes.command.autocomplete;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.homes.world.DisabledWorldHome;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

public class HomeWorldRemoveAutoComplete implements SuggestionProvider<BukkitCommandActor> {
    private final DisabledWorldHome disabledWorldHome = OMCRegistry.HOME_FEATURES.DISABLED_WORLD_HOME;

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return new ArrayList<>(disabledWorldHome.getDisabledWorlds());
    }
}