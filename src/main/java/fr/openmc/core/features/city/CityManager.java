package fr.openmc.core.features.city;

import fr.openmc.core.features.city.events.ChunkClaimedEvent;
import fr.openmc.core.features.city.events.CityCreationEvent;
import fr.openmc.core.features.city.events.CityDeleteEvent;
import fr.openmc.core.utils.BlockVector2;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.city.commands.*;
import fr.openmc.core.features.city.listeners.*;
import fr.openmc.core.utils.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CityManager implements Listener {
    private static HashMap<String, City> cities = new HashMap<>();
    private static HashMap<UUID, City> playerCities = new HashMap<>();
    public static HashMap<BlockVector2, City> claimedChunks = new HashMap<>();

    public CityManager() {
        OMCPlugin.registerEvents(this);

        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("city_members", ((args, sender, command) -> {
            String playerCity = playerCities.get(sender.getUniqueId()).getUUID();

            if (playerCity == null) return List.of();

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                try {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT * FROM city_regions");
                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        claimedChunks.put(BlockVector2.at(rs.getInt(1), rs.getInt(2)), getCity(rs.getString("city_uuid")));
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
                new CityChestCommand()
        );

        OMCPlugin.registerEvents(
                new ProtectionListener(),
                new ChestMenuListener()
        );
    }

    @EventHandler
    public void onChunkClaim(ChunkClaimedEvent event) {
        claimedChunks.put(BlockVector2.at(event.getChunk().getX(), event.getChunk().getZ()), event.getCity());
    }

    public static Collection<City> getCities() {
        return cities.values();
    }

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city (uuid VARCHAR(8) NOT NULL PRIMARY KEY, owner VARCHAR(36) NOT NULL, name VARCHAR(32), balance DOUBLE DEFAULT 0);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_members (city_uuid VARCHAR(8) NOT NULL, player VARCHAR(36) NOT NULL PRIMARY KEY);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_permissions (city_uuid VARCHAR(8) NOT NULL, player VARCHAR(36) NOT NULL, permission VARCHAR(255) NOT NULL);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_chests (city_uuid VARCHAR(8) NOT NULL, page TINYINT UNSIGNED NOT NULL, content LONGBLOB);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_regions (city_uuid VARCHAR(8) NOT NULL, x MEDIUMINT NOT NULL, z MEDIUMINT NOT NULL);").executeUpdate(); // Faut esperer qu'aucun clodo n'ira à 134.217.712 blocks du spawn
    }

    public static boolean isChunkClaimed(int x, int z) {
        return getCityFromChunk(x, z) != null;
    }

    @Nullable
    public static City getCityFromChunk(int x, int z) {
        if (claimedChunks.containsKey(BlockVector2.at(x, z))) {
            return claimedChunks.get(BlockVector2.at(x, z));
        }

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid FROM city_regions WHERE x = ? AND z = ? LIMIT 1");
            statement.setInt(1, x);
            statement.setInt(2, z);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                claimedChunks.put(BlockVector2.at(x, z), null);
                return null;
            }

            claimedChunks.put(BlockVector2.at(x, z), CityManager.getCity(rs.getString("city_uuid")));
            return claimedChunks.get(BlockVector2.at(x, z));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static City createCity(Player owner, String city_uuid, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city VALUE (?, ?, ?, 0)");
                statement.setString(1, city_uuid);
                statement.setString(2, owner.getUniqueId().toString());
                statement.setString(3, name);
                statement.executeUpdate();

                statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_chests VALUE (?, 1, null)");
                statement.setString(1, city_uuid);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        City city = new City(city_uuid);
        Bukkit.getPluginManager().callEvent(new CityCreationEvent(city, owner));
        return city;
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
        City cityz = cities.remove(city);

        for (BlockVector2 vector : claimedChunks.keySet()) {
            if (claimedChunks.get(vector).equals(cityz)) {
                claimedChunks.remove(vector);
            }
        }

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
