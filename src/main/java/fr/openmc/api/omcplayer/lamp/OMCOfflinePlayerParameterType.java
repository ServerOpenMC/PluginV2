package fr.openmc.api.omcplayer.lamp;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.parameters.PlayerParameterType;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public class OMCOfflinePlayerParameterType implements ParameterType<BukkitCommandActor, OMCOfflinePlayer> {

    private final PlayerParameterType delegate = new PlayerParameterType(false);

    @Override
    public OMCOfflinePlayer parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        return OMCOfflinePlayer.of(delegate.parse(input, context));
    }

    @Override
    public @NotNull SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return delegate.defaultSuggestions();
    }
}
