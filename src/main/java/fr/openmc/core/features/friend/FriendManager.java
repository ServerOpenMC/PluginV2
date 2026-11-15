package fr.openmc.core.features.friend;

import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.features.friend.commands.FriendCommand;
import lombok.Getter;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Credit(developers = {"Axeno"})
public class FriendManager extends Feature implements HasDatabase, HasCommands {

    @Getter
    public static final List<FriendRequest> friendsRequests = new ArrayList<>();

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new FriendCommand()
        );
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        FriendSQLManager.initDB(connectionSource);
    }

    public static CompletableFuture<List<UUID>> getFriendsAsync(UUID playerUUID) {
        return FriendSQLManager.getAllFriendsAsync(playerUUID);
    }

    public static void addFriend(UUID senderUUID, UUID receiverUUID) {
        FriendSQLManager.addInDatabase(senderUUID, receiverUUID);
        removeRequest(getRequest(senderUUID, receiverUUID));
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

    public static FriendRequest getRequest(UUID senderUUID, UUID receiverUUID) {
        for (FriendRequest request : friendsRequests) {
            if (request.getSenderUUID().equals(senderUUID) && request.getReceiverUUID().equals(receiverUUID)) {
                return request;
            }
        }
        return null;
    }

    public static boolean hasRequestBetween(UUID senderUUID, UUID receiverUUID) {
        return friendsRequests.stream().anyMatch(request ->
                (request.getSenderUUID().equals(senderUUID) && request.getReceiverUUID().equals(receiverUUID)) ||
                        (request.getSenderUUID().equals(receiverUUID) && request.getReceiverUUID().equals(senderUUID))
        );
    }

    public static boolean hasReceivedRequestFrom(UUID receiverUUID, UUID senderUUID) {
        return friendsRequests.stream().anyMatch(request ->
                request.getSenderUUID().equals(senderUUID) && request.getReceiverUUID().equals(receiverUUID)
        );
    }

    public static boolean isRequestPending(UUID uuid) {
        return friendsRequests.stream().anyMatch(request -> request.containsUUID(uuid));
    }
}
