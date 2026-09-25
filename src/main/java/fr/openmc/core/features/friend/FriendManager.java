package fr.openmc.core.features.friend;

import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityFeaturesRegistry;
import fr.openmc.core.features.friend.commands.FriendCommand;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasDatabase;
import fr.openmc.core.lifecycle.interfaces.HasRegistries;
import fr.openmc.core.lifecycle.registries.LifecycleRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import lombok.Getter;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Credit(developers = {"Axeno"})
public class FriendManager extends Feature implements HasCommands, HasRegistries {

    // TODO: Configuration pour activer/désactiver les demandes d'amis (par défaut activé) & les messages de connexion/déconnexion
    // Config: accepter que les joueurs voient l'argent, la ville, le status (En ligne, Hors ligne), le temps de jeu, ou autre

    @Getter
    public final List<FriendRequest> friendsRequests = new ArrayList<>();

    private FriendSQLManager friendSQLManager;

    @Override
    public void init() {
        friendSQLManager = OMCRegistry.FRIEND_FEATURES.FRIEND_DB;
    }


    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new FriendCommand()
        );
    }

    @Override
    public List<Supplier<LifecycleRegistry>> getRegistries() {
        return new ArrayList<>(List.of(
                () -> SubRegistry.boot(new FriendsFeaturesRegistry(),
                        r -> OMCRegistry.FRIEND_FEATURES = r)
        ));
    }

    public CompletableFuture<List<UUID>> getFriendsAsync(UUID playerUUID) {
        return friendSQLManager.getAllFriendsAsync(playerUUID);
    }

    public void addFriend(UUID firstUUID, UUID secondUUID) {
        friendSQLManager.addInDatabase(firstUUID, secondUUID);
        removeRequest(getRequest(firstUUID));
    }

    public boolean removeFriend(UUID firstUUID, UUID secondUUID) {
        return friendSQLManager.removeInDatabase(firstUUID, secondUUID);
    }

    public boolean areFriends(UUID firstUUID, UUID secondUUID) {
        return friendSQLManager.areFriends(firstUUID, secondUUID);
    }

    public Timestamp getTimestamp(UUID firstUUID, UUID secondUUID) {
        return friendSQLManager.getTimestamp(firstUUID, secondUUID);
    }

    public void addRequest(UUID firstUUID, UUID secondUUID) {
        if (isRequestPending(firstUUID)) {
            return;
        }

        FriendRequest friendsRequest = new FriendRequest(firstUUID, secondUUID);
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
