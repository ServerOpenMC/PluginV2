package fr.openmc.core.features.friend;

import fr.openmc.core.utils.database.DatabaseManager;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class FriendSQLManager {

    private static final String TABLE_NAME = "friends";

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS friends (" +
                "firstPlayer_uuid VARCHAR(36) NOT NULL," +
                "secondPlayer_uuid VARCHAR(36) NOT NULL," +
                "friendDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "bestFriend BOOLEAN NOT NULL DEFAULT FALSE" +
                ")").executeUpdate();
    }

    public static boolean addInDatabase(UUID firstUUID, UUID secondUUID) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO " + TABLE_NAME + " (firstPlayer_uuid, secondPlayer_uuid, friendDate, bestFriend) VALUES (?, ?, ?, ?)");

            statement.setString(1, firstUUID.toString());
            statement.setString(2, secondUUID.toString());
            statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            statement.setBoolean(4, false);

            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }

    public static boolean removeInDatabase(UUID firstUUID, UUID secondUUID) {
        try {

            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE (firstPlayer_uuid = ? AND secondPlayer_uuid = ?) OR (firstPlayer_uuid = ? AND secondPlayer_uuid = ?)");

            statement.setString(1, firstUUID.toString());
            statement.setString(2, secondUUID.toString());
            statement.setString(3, secondUUID.toString());
            statement.setString(4, firstUUID.toString());

            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }

    public static boolean areFriends(UUID firstUUID, UUID secondUUID) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT firstPlayer_uuid, secondPlayer_uuid FROM " + TABLE_NAME + " WHERE (firstPlayer_uuid = ? AND secondPlayer_uuid = ?) OR (firstPlayer_uuid = ? AND secondPlayer_uuid = ?)");

            statement.setString(1, firstUUID.toString());
            statement.setString(2, secondUUID.toString());
            statement.setString(3, secondUUID.toString());
            statement.setString(4, firstUUID.toString());

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }

    public static boolean isBestFriend(UUID firstUUID, UUID secondUUID) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT bestFriend FROM " + TABLE_NAME + " WHERE (firstPlayer_uuid = ? AND secondPlayer_uuid = ?) OR (firstPlayer_uuid = ? AND secondPlayer_uuid = ?)");

            statement.setString(1, firstUUID.toString());
            statement.setString(2, secondUUID.toString());
            statement.setString(3, secondUUID.toString());
            statement.setString(4, firstUUID.toString());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("bestFriend");
            }
            return false;
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return false;
    }

    public static boolean setBestFriend(UUID firstUUID, UUID secondUUID, boolean bestFriend) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE " + TABLE_NAME + " SET bestFriend = ? WHERE (firstPlayer_uuid = ? AND secondPlayer_uuid = ?) OR (firstPlayer_uuid = ? AND secondPlayer_uuid = ?)");

            statement.setBoolean(1, bestFriend);
            statement.setString(2, firstUUID.toString());
            statement.setString(3, secondUUID.toString());
            statement.setString(4, secondUUID.toString());
            statement.setString(5, firstUUID.toString());

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }

    public static Timestamp getTimestamp(UUID firstUUID, UUID secondUUID) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT friendDate FROM " + TABLE_NAME + " WHERE (firstPlayer_uuid = ? AND secondPlayer_uuid = ?) OR (firstPlayer_uuid = ? AND secondPlayer_uuid = ?)");

            statement.setString(1, firstUUID.toString());
            statement.setString(2, secondUUID.toString());
            statement.setString(3, secondUUID.toString());
            statement.setString(4, firstUUID.toString());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getTimestamp("friendDate");
            }

        } catch (SQLException e) {
            System.out.println(e.toString());
        }

        return null;
    }

    public static CompletableFuture<List<UUID>> getAllFriendsAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            List<UUID> friends = new ArrayList<>();

            try {
                String sql = "SELECT * FROM " + TABLE_NAME + " WHERE firstPlayer_uuid = ? OR secondPlayer_uuid = ?";
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(sql);
                statement.setString(1, uuid.toString());
                statement.setString(2, uuid.toString());

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String friendUUID = resultSet.getString("firstPlayer_uuid").equals(uuid.toString()) ? resultSet.getString("secondPlayer_uuid") : resultSet.getString("firstPlayer_uuid");
                    friends.add(UUID.fromString(friendUUID));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return friends;
        });
    }

    public static CompletableFuture<List<UUID>> getBestFriendsAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            List<UUID> friends = new ArrayList<>();

            try {
                String sql = "SELECT * FROM " + TABLE_NAME + " WHERE (firstPlayer_uuid = ? OR secondPlayer_uuid = ?) AND bestFriend = TRUE";
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(sql);
                statement.setString(1, uuid.toString());
                statement.setString(2, uuid.toString());

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String friendUUID = resultSet.getString("firstPlayer_uuid").equals(uuid.toString()) ? resultSet.getString("secondPlayer_uuid") : resultSet.getString("firstPlayer_uuid");
                    friends.add(UUID.fromString(friendUUID));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return friends;
        });
    }
}
