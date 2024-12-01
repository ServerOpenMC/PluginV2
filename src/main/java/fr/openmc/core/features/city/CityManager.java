package fr.openmc.core.features.city;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.city.commands.*;
import fr.openmc.core.features.city.listeners.*;
import fr.openmc.core.utils.database.DatabaseManager;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CityManager {
    private static HashMap<String, City> cities = new HashMap<>();
    private static HashMap<UUID, City> playerCities = new HashMap<>();

    public CityManager() {
        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("city_members", ((args, sender, command) -> {
            String playerCity = playerCities.get(sender.getUniqueId()).getUUID();

            if (playerCity == null) return List.of();

            return playerCities.keySet().stream()
                    .filter(uuid -> playerCities.get(uuid).getUUID().equals(playerCity))
                    .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                    .collect(Collectors.toList());
        }));

        CommandsManager.getHandler().register(
                new CityCommands(),
                new AdminCityCommands(),
                new CityPermsCommands(),
                new CityChatCommand()
        );

        OMCPlugin.registerEvents(
                new ProtectionListener()
        );
    }

    public static Collection<City> getCities() {
        return cities.values();
    }

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city (uuid VARCHAR(8) NOT NULL PRIMARY KEY, owner VARCHAR(36) NOT NULL, bank_pages TINYINT UNSIGNED, name VARCHAR(32), balance DOUBLE DEFAULT 0);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_members (city_uuid VARCHAR(8) NOT NULL, player VARCHAR(36) NOT NULL PRIMARY KEY);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_permissions (city_uuid VARCHAR(8) NOT NULL, player VARCHAR(36) NOT NULL, permission VARCHAR(255) NOT NULL);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_regions (city_uuid VARCHAR(8) NOT NULL, x MEDIUMINT NOT NULL, z MEDIUMINT NOT NULL);").executeUpdate(); // Faut esperer qu'aucun clodo n'ira à 134.217.712 blocks du spawn
    }

    public static boolean isChunkClaimed(int x, int z) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid FROM city_regions WHERE x = ? AND z = ? LIMIT 1");
            statement.setInt(1, x);
            statement.setInt(2, z);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static City createCity(UUID owner, String city, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city VALUE (?, ?, ?, ?, 0)");
                statement.setString(1, city);
                statement.setString(2, owner.toString());
                statement.setInt(3, 1);
                statement.setString(4, name);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return new City(city);
    }

    public static void registerCity(City city) {
        cities.put(city.getUUID(), city);
    }

    public static City getCity(String city) {
        if (!cities.containsKey(city)) {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT uuid FROM city WHERE uuid = ? LIMIT 1");
                statement.setString(1, city);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    City c = new City(city);
                    cities.put(c.getUUID(), c);
                    return c;
                }

                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return cities.get(city);
    }

    public static void forgetCity(String city) {
        cities.remove(city);

        for (UUID uuid : playerCities.keySet()) {
            if (playerCities.get(uuid).getUUID().equals(city)) {
                playerCities.remove(uuid);
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("DELETE FROM city_permissions WHERE city_uuid = ?");
                statement.setString(1, city);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void cachePlayer(UUID uuid, City city) {
        playerCities.put(uuid, city);
    }

    public static City getPlayerCity(UUID uuid) {
        if (!playerCities.containsKey(uuid)) {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid FROM city_members WHERE player = ? LIMIT 1");
                statement.setString(1, uuid.toString());
                ResultSet rs = statement.executeQuery();

                if (!rs.next()) {
                    return null;
                }


                String city = rs.getString(1);
                cachePlayer(uuid, getCity(city));
                return getCity(city);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return playerCities.get(uuid);
    }

    public static void uncachePlayer(UUID uuid) {
        playerCities.remove(uuid);
    }
}
