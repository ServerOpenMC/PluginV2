package fr.openmc.api.entity.player;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.parameters.PlayerParameterType;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public class OMCPlayerParameterType implements ParameterType<BukkitCommandActor, OMCPlayer> {

    private final PlayerParameterType delegate = new PlayerParameterType(false);

    @Override
    public OMCPlayer parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        return OMCPlayer.of(delegate.parse(input, context));
    }

    @Override
    public @NotNull SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return delegate.defaultSuggestions();
    }
}
