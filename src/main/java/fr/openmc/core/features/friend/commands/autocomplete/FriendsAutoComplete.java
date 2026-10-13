package fr.openmc.core.features.friend.commands.autocomplete;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;
import java.util.UUID;

public class FriendsAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        OMCPlayer player = OMCPlayer.of(context.actor().requirePlayer());
        List<UUID> friendsUUIDs = player.friends().getFriendsAsync().join();
        return friendsUUIDs.stream()
                .map(uuid -> CacheOfflinePlayer.getOfflinePlayer(uuid).getName())
                .filter(name -> !context.actor().requirePlayer().hasMetadata(OMCPlugin.VANISH_META_KEY))
                .toList();
    }
}
