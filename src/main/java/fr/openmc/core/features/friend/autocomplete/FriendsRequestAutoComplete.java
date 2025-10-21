package fr.openmc.core.features.friend.autocomplete;

import fr.openmc.core.utils.CacheOfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;
import java.util.UUID;

import static fr.openmc.core.features.friend.FriendManager.friendsRequests;

public class FriendsRequestAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        Player sender = context.actor().requirePlayer();

        List<UUID> requestUUIDs = friendsRequests.stream()
                .filter(request -> request.containsUUID(sender.getUniqueId()))
                .map(request -> request.getSenderUUID().equals(sender.getUniqueId()) ? request.getReceiverUUID() : request.getSenderUUID())
                .toList();
        return requestUUIDs.stream()
                .map(uuid -> CacheOfflinePlayer.getOfflinePlayer(uuid).getName())
                .toList();
    }
}
