package fr.openmc.core.features.friend.commands.autocomplete;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;
import java.util.UUID;

public class FriendsRequestAutoComplete implements SuggestionProvider<BukkitCommandActor> {
    private final FriendManager friendManager = OMCRegistry.FEATURES.FRIENDS.get();

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        Player sender = context.actor().requirePlayer();

        List<UUID> requestUUIDs = friendManager.friendsRequests.stream()
                .filter(request -> request.containsUUID(sender.getUniqueId()))
                .map(request -> request.getSenderUUID().equals(sender.getUniqueId()) ? request.getReceiverUUID() : request.getSenderUUID())
                .toList();
        return requestUUIDs.stream()
                .map(uuid -> CacheOfflinePlayer.getOfflinePlayer(uuid).getName())
                .filter(name -> !sender.hasMetadata(OMCPlugin.VANISH_META_KEY))
                .toList();
    }
}
