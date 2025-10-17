package fr.openmc.core.features.friend;

import lombok.Getter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FriendManager {

    // TODO: Configuration pour activer/désactiver les demandes d'amis (par défaut activé) & les messages de connexion/déconnexion
    // Config: accepter que les joueurs voient l'argent, la ville, le status (En ligne, Hors ligne), le temps de jeu, ou autre

    @Getter
    public static final List<FriendRequest> friendsRequests = new ArrayList<>();

    public static CompletableFuture<List<UUID>> getFriendsAsync(UUID playerUUID) {
        return FriendSQLManager.getAllFriendsAsync(playerUUID);
    }

    public static void addFriend(UUID firstUUID, UUID secondUUID) {
        FriendSQLManager.addInDatabase(firstUUID, secondUUID);
        removeRequest(getRequest(firstUUID));
    }

    public static boolean removeFriend(UUID firstUUID, UUID secondUUID) {
        return FriendSQLManager.removeInDatabase(firstUUID, secondUUID);
    }

    public static boolean areFriends(UUID firstUUID, UUID secondUUID) {
        return FriendSQLManager.areFriends(firstUUID, secondUUID);
    }

    public static Timestamp getTimestamp(UUID firstUUID, UUID secondUUID) {
        return FriendSQLManager.getTimestamp(firstUUID, secondUUID);
    }

    public static void addRequest(UUID firstUUID, UUID secondUUID) {
        if (isRequestPending(firstUUID)) {
            return;
        }

        FriendRequest friendsRequest = new FriendRequest(firstUUID, secondUUID);
        friendsRequest.sendRequest();
        friendsRequests.add(friendsRequest);
    }

    public static void removeRequest(FriendRequest friendsRequest) {
        if (friendsRequest != null) {
            if (!friendsRequest.isCancelled()) {
                friendsRequest.cancel();
            }
        }

        friendsRequests.remove(friendsRequest);
    }

    public static FriendRequest getRequest(UUID uuid) {
        for (FriendRequest friendsRequests : friendsRequests) {
            if (friendsRequests.containsUUID(uuid)) {
                return friendsRequests;
            }
        }
        return null;
    }

    public static boolean isRequestPending(UUID uuid) {
        return friendsRequests.stream().anyMatch(request -> request.containsUUID(uuid));
    }
}
