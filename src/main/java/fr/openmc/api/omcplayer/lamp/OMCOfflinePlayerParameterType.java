package fr.openmc.api.omcplayer.lamp;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public class OMCOfflinePlayerParameterType implements ParameterType<BukkitCommandActor, OMCOfflinePlayer> {

    @Override
    public OMCOfflinePlayer parse(@NotNull MutableStringStream input,
                                  @NotNull ExecutionContext<BukkitCommandActor> context) {
        String name = input.readString();
        OfflinePlayer player = CacheOfflinePlayer.getOfflinePlayer(name);

        if (player == null) throw new InvalidPlayerException(name);
        return OMCOfflinePlayer.of(player);
    }

    @Override
    public @NotNull SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return _ -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .toList();
    }
}
