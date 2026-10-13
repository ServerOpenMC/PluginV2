package fr.openmc.api.omcplayer.sub;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.feature.OMCOfflinePlayerFeat;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.friend.FriendRequest;
import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class OMCPlayerFriends extends OMCOfflinePlayerFeat {
    private final FriendManager friendManager = OMCRegistry.FEATURES.FRIENDS.get();

    public OMCPlayerFriends(OfflinePlayer player) {
        super(player);
    }

    public CompletableFuture<List<UUID>> getFriendsAsync() {
        return friendManager.getFriendsAsync(getUniqueId());
    }

    public void addFriend(UUID targetUUID) {
        friendManager.addFriend(getUniqueId(), targetUUID);
    }

    public void addFriend(OMCOfflinePlayer target) {
        addFriend(target.getUniqueId());
    }

    public boolean removeFriend(UUID targetUUID) {
        return friendManager.removeFriend(getUniqueId(), targetUUID);
    }

    public boolean removeFriend(OMCOfflinePlayer target) {
        return removeFriend(target.getUniqueId());
    }

    public boolean areFriends(UUID targetUUID) {
        return friendManager.areFriends(getUniqueId(), targetUUID);
    }

    public boolean areFriends(OMCOfflinePlayer target) {
        return areFriends(target.getUniqueId());
    }

    public Timestamp getTimestamp(UUID targetUUID) {
        return friendManager.getTimestamp(getUniqueId(), targetUUID);
    }

    public Timestamp getTimestamp(OMCOfflinePlayer target) {
        return getTimestamp(target.getUniqueId());
    }

    public void addRequest(UUID targetUUID) {
        friendManager.addRequest(getUniqueId(), targetUUID);
    }

    public void addRequest(OMCOfflinePlayer target) {
        addRequest(target.getUniqueId());
    }

    public void removeRequest(FriendRequest friendsRequest) {
        friendManager.removeRequest(friendsRequest);
    }

    public FriendRequest getRequest() {
        return friendManager.getRequest(getUniqueId());
    }

    public boolean isRequestPending() {
        return friendManager.isRequestPending(getUniqueId());
    }

    public List<FriendRequest> getFriendsRequests() {
        return friendManager.getFriendsRequests();
    }
}
