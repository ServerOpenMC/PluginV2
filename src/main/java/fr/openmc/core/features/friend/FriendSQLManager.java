package fr.openmc.core.features.friend;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FriendSQLManager {

    private static Dao<Friend, UUID> friendsDao;

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Friend.class);
        friendsDao = DaoManager.createDao(connectionSource, Friend.class);
    }

    private static Friend getFriendObject(UUID first, UUID second) {
        try {
            QueryBuilder<Friend, UUID> query = friendsDao.queryBuilder();
            Where<Friend, UUID> where = query.where();

            where.or(where.eq("first", first).and().eq("second", second), where.eq("first", second).and().eq("second"
                    , first)); // Bordel !!!!

            List<Friend> objs = friendsDao.query(query.prepare());

            if (objs.isEmpty()) {
                return null;
            } else {
                return objs.getFirst();
            }

        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Failed to get Friends Object 1={} 2={}", first, second, e);
            return null;
        }
    }

    public static boolean addInDatabase(UUID first, UUID second) {
        try {
            return friendsDao.create(new Friend(first, second, Timestamp.valueOf(LocalDateTime.now()))) != 0;
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Failed to add Friends in database", e);
            return false;
        }
    }

    public static boolean removeInDatabase(UUID first, UUID second) {
        try {
            return friendsDao.delete(getFriendObject(first, second)) != 0;
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Failed to remove Friends in database", e);
            return false;
        }
    }

    public static boolean areFriends(UUID first, UUID second) {
        return getFriendObject(first, second) != null;
    }

    public static boolean isBestFriend(UUID first, UUID second) {
        return getFriendObject(first, second).isBestFriend();
    }

    public static boolean setBestFriend(UUID first, UUID second, boolean bestFriend) {
        Friend friend = getFriendObject(first, second);
        friend.setBestFriend(bestFriend);
        try {
            return friendsDao.update(friend) != 0;
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Failed to set Best Friends in database", e);
            return false;
        }
    }

    public static Timestamp getTimestamp(UUID first, UUID second) {
        return getFriendObject(first, second).getDate();
    }

    public static CompletableFuture<List<UUID>> getAllFriendsAsync(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            List<UUID> friends = new ArrayList<>();

            try {
                QueryBuilder<Friend, UUID> query = friendsDao.queryBuilder();
                query.where().eq("first", playerUUID).or().eq("second", playerUUID);
                friendsDao.query(query.prepare()).forEach(friend -> friends.add(friend.getOther(playerUUID)));
            } catch (SQLException e) {
                OMCPlugin.getInstance().getSLF4JLogger().error("Failed to get a friends async", e);
            }
            return friends;
        });
    }

    public static CompletableFuture<List<UUID>> getBestFriendsAsync(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            List<UUID> friends = new ArrayList<>();

            try {
                QueryBuilder<Friend, UUID> query = friendsDao.queryBuilder();
                query.where().and(query.where().eq("first", playerUUID)
                                .or().eq("second", playerUUID),
                        query.where().eq("best_friend", true));
                friendsDao.query(query.prepare()).forEach(friend -> friends.add(friend.getOther(playerUUID)));
            } catch (SQLException e) {
                OMCPlugin.getInstance().getSLF4JLogger().error("Failed to get Best Friends async", e);
            }
            return friends;
        });
    }
}
