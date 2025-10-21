package fr.openmc.core.features.homes.command.autocomplete;

import fr.openmc.core.features.homes.world.DisabledWorldHome;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

public class HomeWorldRemoveAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return new ArrayList<>(DisabledWorldHome.getDisabledWorlds());
    }
}