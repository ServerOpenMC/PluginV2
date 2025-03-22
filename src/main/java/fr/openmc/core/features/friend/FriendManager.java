package fr.openmc.core.features.friend;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class FriendManager {

    // TODO: Configuration pour activer/désactiver les demandes d'amis (par défaut activé) & les messages de connexion/déconnexion
    // Config: accepter que les joueurs voient l'argent, la ville, le status (En ligne, Hors ligne), le temps de jeu, ou autre

    private final OMCPlugin plugin = OMCPlugin.getInstance();;
    public static FriendManager instance;

    @Getter private final List<FriendRequest> friendsRequests = new ArrayList<>();

    public void initCommandSuggestion() {
        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("friends", (args, sender, command) -> {
            List<UUID> friendsUUIDs = getFriendsAsync(sender.getUniqueId()).join();
            return friendsUUIDs.stream()
                    .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                    .toList();
        });

        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("friends-request", (args, sender, command) -> {
            List<UUID> requestUUIDs = friendsRequests.stream()
                    .filter(request -> request.containsUUID(sender.getUniqueId()))
                    .map(request -> request.getSenderUUID().equals(sender.getUniqueId()) ? request.getReceiverUUID() : request.getSenderUUID())
                    .toList();
            return requestUUIDs.stream()
                    .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                    .toList();
        });
    }

    public static FriendManager getInstance() {
        if (instance == null) {
            instance = new FriendManager();
        }
        return instance;
    }

    public CompletableFuture<List<UUID>> getFriendsAsync(UUID playerUUID) {
        return FriendSQLManager.getAllFriendsAsync(playerUUID);
    }

    public void addFriend(UUID firstUUID, UUID secondUUID) {
        FriendSQLManager.addInDatabase(firstUUID, secondUUID);
        removeRequest(getRequest(firstUUID));
    }

    public void removeFriend(UUID firstUUID, UUID secondUUID) {
        FriendSQLManager.removeInDatabase(firstUUID, secondUUID);
    }

    public boolean areFriends(UUID firstUUID, UUID secondUUID) {
        return FriendSQLManager.areFriends(firstUUID, secondUUID);
    }

    public Timestamp getTimestamp(UUID firstUUID, UUID secondUUID) {
        return FriendSQLManager.getTimestamp(firstUUID, secondUUID);
    }

    public void addRequest(UUID firstUUID, UUID secondUUID) {
        if (isRequestPending(firstUUID)) {
            return;
        }

        FriendRequest friendsRequest = new FriendRequest(this, plugin, firstUUID, secondUUID);
        friendsRequest.sendRequest();
        friendsRequests.add(friendsRequest);
    }

    public void removeRequest(FriendRequest friendsRequest) {
        if (friendsRequest != null) {
            if (!friendsRequest.isCancelled()) {
                friendsRequest.cancel();
            }
        }

        friendsRequests.remove(friendsRequest);
    }

    public FriendRequest getRequest(UUID uuid) {
        for (FriendRequest friendsRequests : friendsRequests) {
            if (friendsRequests.containsUUID(uuid)) {
                return friendsRequests;
            }
        }
        return null;
    }

    public boolean isRequestPending(UUID uuid) {
        return friendsRequests.stream().anyMatch(request -> request.containsUUID(uuid));
    }
}
