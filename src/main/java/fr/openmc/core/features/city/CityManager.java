package fr.openmc.core.features.city;

import fr.openmc.core.utils.BlockVector2;
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
    public static HashMap<BlockVector2, Boolean> claimedChunks = new HashMap<>();

    public CityManager() {
        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("city_members", ((args, sender, command) -> {
            String playerCity = playerCities.get(sender.getUniqueId()).getUUID();

            if (playerCity == null) return List.of();

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                try {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT * FROM city_regions");
                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        claimedChunks.put(BlockVector2.at(rs.getInt(1), rs.getInt(2)), true);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            return playerCities.keySet().stream()
                    .filter(uuid -> playerCities.get(uuid).getUUID().equals(playerCity))
                    .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                    .collect(Collectors.toList());
        }));

        CommandsManager.getHandler().register(
                new CityCommands(),
                new AdminCityCommands(),
                new CityPermsCommands(),
                new CityChatCommand(),
                new CityBankCommand()
        );

        OMCPlugin.registerEvents(
                new ProtectionListener(),
                new BankMenuListener()
        );
    }

    public static Collection<City> getCities() {
        return cities.values();
    }

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city (uuid VARCHAR(8) NOT NULL PRIMARY KEY, owner VARCHAR(36) NOT NULL, name VARCHAR(32), balance DOUBLE DEFAULT 0);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_members (city_uuid VARCHAR(8) NOT NULL, player VARCHAR(36) NOT NULL PRIMARY KEY);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_permissions (city_uuid VARCHAR(8) NOT NULL, player VARCHAR(36) NOT NULL, permission VARCHAR(255) NOT NULL);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_banks (city_uuid VARCHAR(8) NOT NULL, page TINYINT UNSIGNED NOT NULL, content LONGBLOB);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_regions (city_uuid VARCHAR(8) NOT NULL, x MEDIUMINT NOT NULL, z MEDIUMINT NOT NULL);").executeUpdate(); // Faut esperer qu'aucun clodo n'ira à 134.217.712 blocks du spawn
    }

    public static boolean isChunkClaimed(int x, int z) {
        if (claimedChunks.containsKey(BlockVector2.at(x, z))) {
            return claimedChunks.get(BlockVector2.at(x, z));
        }

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid FROM city_regions WHERE x = ? AND z = ? LIMIT 1");
            statement.setInt(1, x);
            statement.setInt(2, z);
            ResultSet rs = statement.executeQuery();
            claimedChunks.put(BlockVector2.at(x, z), rs.next());
            return claimedChunks.get(BlockVector2.at(x, z));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static City createCity(UUID owner, String city, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city VALUE (?, ?, ?, 0)");
                statement.setString(1, city);
                statement.setString(2, owner.toString());
                statement.setString(3, name);
                statement.executeUpdate();

                statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_banks VALUE (?, 1, null)");
                statement.setString(1, city);
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
