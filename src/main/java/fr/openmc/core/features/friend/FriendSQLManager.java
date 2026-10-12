package fr.openmc.core.features.friend;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.db.DBTable;
import fr.openmc.core.lifecycle.interfaces.db.HasDatabase;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.utils.text.DateUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FriendSQLManager extends Feature implements HasDatabase {
    private final DBTable<Friend, UUID> friendsTable = DBTable.of(Friend.class);

    @Override
    public List<DBTable<?, ?>> getTables() {
        return List.of(friendsTable);
    }

    private Friend getFriendObject(UUID first, UUID second) {
        try {
            QueryBuilder<Friend, UUID> query = friendsTable.queryBuilder();
            Where<Friend, UUID> where = query.where();

            where.or(where.eq("first", first).and().eq("second", second), where.eq("first", second).and().eq("second"
                    , first)); // Bordel !!!!

            List<Friend> objs = friendsTable.query(query.prepare());

            if (objs.isEmpty()) {
                return null;
            } else {
                return objs.getFirst();
            }

        } catch (SQLException e) {
            OMCLogger.error("Failed to get Friends Object 1={} 2={}", first, second, e);
            return null;
        }
    }

    public boolean addInDatabase(UUID first, UUID second) {
        try {
            return friendsTable.create(new Friend(first, second, Timestamp.valueOf(DateUtils.getLocalDateTime()))) != 0;
        } catch (SQLException e) {
            OMCLogger.error("Failed to add Friends in database", e);
            return false;
        }
    }

    public boolean removeInDatabase(UUID first, UUID second) {
        try {
            return friendsTable.delete(getFriendObject(first, second)) != 0;
        } catch (SQLException e) {
            OMCLogger.error("Failed to remove Friends in database", e);
            return false;
        }
    }

    public boolean areFriends(UUID first, UUID second) {
        return getFriendObject(first, second) != null;
    }

    public boolean isBestFriend(UUID first, UUID second) {
        return getFriendObject(first, second).isBestFriend();
    }

    public boolean setBestFriend(UUID first, UUID second, boolean bestFriend) {
        Friend friend = getFriendObject(first, second);
        friend.setBestFriend(bestFriend);
        try {
            return friendsTable.update(friend) != 0;
        } catch (SQLException e) {
            OMCLogger.error("Failed to set Best Friends in database", e);
            return false;
        }
    }

    public Timestamp getTimestamp(UUID first, UUID second) {
        return getFriendObject(first, second).getDate();
    }

    public CompletableFuture<List<UUID>> getAllFriendsAsync(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            List<UUID> friends = new ArrayList<>();

            try {
                QueryBuilder<Friend, UUID> query = friendsTable.queryBuilder();
                query.where().eq("first", playerUUID).or().eq("second", playerUUID);
                friendsTable.query(query.prepare()).forEach(friend -> friends.add(friend.getOther(playerUUID)));
            } catch (SQLException e) {
                OMCLogger.error("Failed to get a friends async", e);
            }
            return friends;
        });
    }

    public CompletableFuture<List<UUID>> getBestFriendsAsync(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            List<UUID> friends = new ArrayList<>();

            try {
                QueryBuilder<Friend, UUID> query = friendsTable.queryBuilder();
                query.where().and(query.where().eq("first", playerUUID)
                                .or().eq("second", playerUUID),
                        query.where().eq("best_friend", true));
                friendsTable.query(query.prepare()).forEach(friend -> friends.add(friend.getOther(playerUUID)));
            } catch (SQLException e) {
                OMCLogger.error("Failed to get Best Friends async", e);
            }
            return friends;
        });
    }
}
