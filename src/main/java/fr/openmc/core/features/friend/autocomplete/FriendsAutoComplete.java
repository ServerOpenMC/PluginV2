package fr.openmc.core.features.friend.autocomplete;

import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;
import java.util.UUID;

import static fr.openmc.core.features.friend.FriendManager.getFriendsAsync;

public class FriendsAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        List<UUID> friendsUUIDs = getFriendsAsync(context.actor().requirePlayer().getUniqueId()).join();
        return friendsUUIDs.stream()
                .map(uuid -> CacheOfflinePlayer.getOfflinePlayer(uuid).getName())
                .toList();
    }
}
