package fr.openmc.core.features.city;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.listeners.CityDoorsListener;
import fr.openmc.core.utils.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CityManager {
    private static HashMap<UUID, String> playerCity = new HashMap<>();
    private static HashMap<String, UUID> cityOwners = new HashMap<>();
    private static HashMap<String, String> cityNames = new HashMap<>();
    private static HashMap<String, ArrayList<UUID>> members = new HashMap<>();

    public CityManager() {
        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("city_members", ((args, sender, command) -> {
            String playerCity = CityManager.getPlayerCity(sender.getUniqueId());

            if (playerCity == null) {
                return List.of();
            }

            return CityManager.getMembers(playerCity).stream()
                    .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                    .collect(Collectors.toList());
        }));

        CommandsManager.getHandler().register(
                new CityCommands()
        );

        OMCPlugin.registerEvents(
                new CityDoorsListener()
        );
    }

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city (uuid VARCHAR(8) NOT NULL PRIMARY KEY , owner VARCHAR(36) NOT NULL, bank_pages TINYINT UNSIGNED, name VARCHAR(32));").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_members (city_uuid VARCHAR(8) NOT NULL , player VARCHAR(36) NOT NULL PRIMARY KEY);").executeUpdate();
    }

    /**
     * Gets the list of members (UUIDs) of a specific city.
     *
     * @param uuid The UUID of the city.
     * @return A list of UUIDs representing the members of the city.
     */
    public static ArrayList<UUID> getMembers(String uuid) {
        ArrayList<UUID> isIn = members.get(uuid);

        if (members.containsKey(uuid)) return isIn;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT player FROM city_members WHERE city = ?");
            statement.setString(1, uuid);

            ArrayList<UUID> cityMembers = members.getOrDefault(uuid, new ArrayList<>());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                cityMembers.add(UUID.fromString(resultSet.getString(1)));
            }

            members.put(uuid, cityMembers);
            return cityMembers;
        } catch (SQLException err) {
            err.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves the name of a city by its UUID.
     *
     * @param uuid The UUID of the city.
     * @return The name of the city, or null if the city does not exist.
     */
    @Nullable public static String getCityName(String uuid) {
        String isIn = cityNames.get(uuid);

        if (cityNames.containsKey(uuid)) return isIn;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT name FROM city WHERE uuid = ? LIMIT 1");
            statement.setString(1, uuid);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                cityNames.put(uuid, resultSet.getString("name"));
                return resultSet.getString("name");
            }

            cityNames.put(uuid, null);
            return resultSet.getString("name");
        } catch (SQLException err) {
            err.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the owner's UUID for a specific city.
     *
     * @param city The UUID of the city.
     * @return The UUID of the city owner, or null if the city does not exist.
     */
    public static UUID getOwnerUUID(String city) {
        UUID isIn = cityOwners.get(city);

        if (cityOwners.containsKey(city)) return isIn;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT owner FROM city WHERE uuid = ? LIMIT 1");
            statement.setString(1, city);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                UUID owner = UUID.fromString(resultSet.getString(1));
                System.out.println("City owner trouvé!");
                cityOwners.put(city, owner);
                return owner;
            }

            cityOwners.put(city, null);
            return null;
        } catch (SQLException err) {
            err.printStackTrace();
            return null;
        }
    }

    /**
     * Renames a city.
     *
     * @param uuid The UUID of the city.
     * @param newName The new name for the city.
     */
    public static void renameCity(String uuid, String newName) {
        cityNames.put(uuid, newName);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE city SET name=? WHERE uuid=?;");
                statement.setString(1, newName);
                statement.setString(2, uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Creates a new city with a specified owner, UUID, and name.
     *
     * @param owner The UUID of the city's owner.
     * @param city The UUID of the city.
     * @param name The name of the city.
     */
    public static void createCity(UUID owner, String city, String name) {
        cityOwners.put(city, owner);
        cityNames.put(city, name);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city VALUE (?, ?, ?, ?)");
                statement.setString(1, city);
                statement.setString(2, owner.toString());
                statement.setInt(3, 1);
                statement.setString(4, name);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Deletes a city, removing it from records and updating members and regions accordingly.
     *
     * @param city The UUID of the city to delete.
     */
    public static void deleteCity(String city) {
        cityOwners.remove(city);
        cityNames.remove(city);

        for (var entry : playerCity.entrySet()) {
            if (!entry.getValue().equals(city)) continue;
            playerCity.remove(entry.getKey());
        }

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
                assert regionManager != null;
                regionManager.removeRegion("city_"+city);
                regionManager.saveChanges();

                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_members WHERE city_uuid=?");
                statement.setString(1, city);
                statement.executeUpdate();

                statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city WHERE uuid=?");
                statement.setString(1, city);
                statement.executeUpdate();
            } catch (SQLException | StorageException e) {
                e.printStackTrace();
            }
        });
    }

    private static void addToMembers(UUID player, String city) {
        ArrayList<UUID> mem = members.get(city);
        mem.add(player);
        members.put(city, mem);
    }

    private static boolean removeFromMembers(UUID player, String city) {
        ArrayList<UUID> mem = members.get(city);
        boolean success = mem.remove(player);
        members.put(city, mem);
        return success;
    }

    /**
     * Adds a player as a member of a specific city.
     *
     * @param player The UUID of the player to add.
     * @param city The UUID of the city to add the player to.
     */
    public static void playerJoinCity(UUID player, String city) {
        playerCity.put(player, city);
        members.put(city, members.getOrDefault(
                city,
                new ArrayList<>())
        );
        addToMembers(player, city);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
                regionManager.getRegion("city_"+city).getMembers().addPlayer(player);
                regionManager.saveChanges();

                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_members VALUE (?, ?)");
                statement.setString(1, city);
                statement.setString(2, player.toString());
                statement.executeUpdate();
            } catch (SQLException | StorageException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Changes the owner of a city.
     *
     * @param player The UUID of the new owner.
     * @param city The UUID of the city.
     */
    public static void changeOwner(UUID player, String city) {
         cityOwners.put(city, player);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE city SET owner=? WHERE uuid=?;");
                statement.setString(1, player.toString());
                statement.setString(2, city);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Allows a player to leave a city and updates the database and region permissions.
     *
     * @param player The UUID of the player leaving the city.
     * @return True if the player successfully left the city, false otherwise.
     */
    public static boolean playerLeaveCity(UUID player) {
        String city = CityManager.getPlayerCity(player);

        members.put(city, members.getOrDefault(
                city,
                new ArrayList<>())
        );

        if (!removeFromMembers(player, city)) return false;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_members WHERE player=?");
            statement.setString(1, player.toString());
            statement.executeUpdate();

            RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
            regionManager.getRegion("city_"+ playerCity.get(player)).getMembers().removePlayer(player);

            regionManager.saveChanges();
            playerCity.remove(player);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Retrieves the city UUID that a player belongs to.
     *
     * @param player The UUID of the player.
     * @return The UUID of the city the player belongs to, or null if they are not part of any city.
     */
    @Nullable public static String getPlayerCity(UUID player) {
        String isIn = playerCity.get(player);

        if (playerCity.containsKey(player)) return isIn;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid FROM city_members WHERE player = ? LIMIT 1");
            statement.setString(1, player.toString());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                playerCity.put(player, resultSet.getString("city_uuid"));
                return resultSet.getString("city_uuid");
            }

            playerCity.put(player, null);
            return null;
        } catch (SQLException err) {
            err.printStackTrace();
            return null;
        }
    }
}
