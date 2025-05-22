package fr.openmc.core.features.friend;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class FriendSQLManager {

    private static Dao<Friend, UUID> friendsDao;

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Friend.class);
        friendsDao = DaoManager.createDao(connectionSource, Friend.class);
    }

    private static Friend getFriendObject(UUID first, UUID second) {
        try {
            QueryBuilder<Friend, UUID> query = friendsDao.queryBuilder();
            query.where().or(query.where().eq("first", first).and().eq("second", second),
                    query.where().eq("first", second).and().eq("second", first));
            return friendsDao.query(query.prepare()).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean addInDatabase(UUID first, UUID second) {
        try {
            return friendsDao.create(new Friend(first, second)) != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeInDatabase(UUID first, UUID second) {
        try {
            return friendsDao.delete(getFriendObject(first, second)) != 0;
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }

    public static Timestamp getTimestamp(UUID first, UUID second) {
        return getFriendObject(first, second).getDate();
    }

    public static CompletableFuture<List<UUID>> getAllFriendsAsync(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            List<UUID> friends = new ArrayList<>();

            try {
                QueryBuilder<Friend, UUID> query = friendsDao.queryBuilder();
                query.where().eq("first", player).or().eq("second", player);
                friendsDao.query(query.prepare()).forEach(friend -> friends.add(friend.getOther(player)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return friends;
        });
    }

    public static CompletableFuture<List<UUID>> getBestFriendsAsync(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            List<UUID> friends = new ArrayList<>();

            try {
                QueryBuilder<Friend, UUID> query = friendsDao.queryBuilder();
                query.where().and(query.where().eq("first", player).or().eq("second", player),
                        query.where().eq("best_friend", true));
                friendsDao.query(query.prepare()).forEach(friend -> friends.add(friend.getOther(player)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return friends;
        });
    }
}
