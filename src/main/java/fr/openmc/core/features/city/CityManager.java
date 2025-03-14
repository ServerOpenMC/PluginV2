package fr.openmc.core.features.city;

import fr.openmc.core.features.city.events.ChunkClaimedEvent;
import fr.openmc.core.features.city.events.CityCreationEvent;
import fr.openmc.core.features.city.mascots.MascotsListener;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.utils.BlockVector2;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.city.commands.*;
import fr.openmc.core.features.city.listeners.*;
import fr.openmc.core.utils.chronometer.Chronometer;
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
                new ChestMenuListener(),
                new MascotsListener()
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
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city (uuid VARCHAR(8) NOT NULL PRIMARY KEY, owner VARCHAR(36) NOT NULL, name VARCHAR(32), balance DOUBLE DEFAULT 0, type VARCHAR(8) NOT NULL);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_members (city_uuid VARCHAR(8) NOT NULL, player VARCHAR(36) NOT NULL PRIMARY KEY);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_permissions (city_uuid VARCHAR(8) NOT NULL, player VARCHAR(36) NOT NULL, permission VARCHAR(255) NOT NULL);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_chests (city_uuid VARCHAR(8) NOT NULL, page TINYINT UNSIGNED NOT NULL, content LONGBLOB);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_regions (city_uuid VARCHAR(8) NOT NULL, x MEDIUMINT NOT NULL, z MEDIUMINT NOT NULL);").executeUpdate();// Faut esperer qu'aucun clodo n'ira à 134.217.712 blocks du spawn
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_power (city_uuid VARCHAR(8) NOT NULL, power_point INT NOT NULL);").executeUpdate();
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

    public static City createCity(Player owner, String cityUUID, String name, String type) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city VALUE (?, ?, ?, 0, ?)");
                statement.setString(1, cityUUID);
                statement.setString(2, owner.getUniqueId().toString());
                statement.setString(3, name);
                statement.setString(4, type);
                statement.executeUpdate();

                statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_chests VALUE (?, 1, null)");
                statement.setString(1, cityUUID);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        City city = new City(cityUUID);
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

        for (UUID members : cityz.getMembers()){
            MascotsManager.removeChest(Bukkit.getPlayer(members));
            if (Chronometer.containsChronometer(members, "Mascot:chest")){
                if (Bukkit.getEntity(members) != null){
                    Chronometer.stopChronometer(Bukkit.getEntity(members), "Mascot:chest", null, "%null%");
                }
            }
            if (Chronometer.containsChronometer(members, "mascotsMove")){
                if (Bukkit.getEntity(members) != null){
                    Chronometer.stopChronometer(Bukkit.getEntity(members), "mascotsMove", null, "%null%");
                }
            }
        }

        Iterator<BlockVector2> iterator = claimedChunks.keySet().iterator();
        while (iterator.hasNext()) {
            BlockVector2 vector = iterator.next();
            City claimedCity = claimedChunks.get(vector);

            if (claimedCity != null && claimedCity.equals(cityz)) {
                iterator.remove();
            }
        }

        Iterator<UUID> playerIterator = playerCities.keySet().iterator();
        while (playerIterator.hasNext()) {
            UUID uuid = playerIterator.next();
            City playerCity = playerCities.get(uuid);

            if (playerCity != null && playerCity.getUUID().equals(city)) {
                playerIterator.remove();
            }
        }

        MascotsManager.freeClaim.remove(city);
        if (CityTypeCooldown.isOnCooldown(city)) {
            CityTypeCooldown.removeCityCooldown(city);
        }
        MascotsManager.removeMascotsFromCity(city);
    }

    public static void changeCityType(String city_uuid) {
        String cityType = getCityType(city_uuid);
        if (cityType != null) {
            cityType = cityType.equals("war") ? "peace" : "war";
        }
        String finalCityType = cityType;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE city SET type=? WHERE uuid=?;");
                statement.setString(1, finalCityType);
                statement.setString(2, city_uuid);
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

    /**
     * return 'war' / 'peace' / 'null' if not found
     */

    public static String getCityType(String city_uuid) {
        String type = null;

        if (city_uuid!=null){
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT type FROM city WHERE uuid = ?");
                statement.setString(1, city_uuid);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    type = rs.getString("type");
                }
            } catch (SQLException e){
                e.printStackTrace();
                return null;
            }
        }

        return type;
    }

    public static int getCityPowerPoints(String city_uuid){
       int power_point = 0;

        if (city_uuid!=null){
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT power_point FROM city_power WHERE city_uuid = ?");
                statement.setString(1, city_uuid);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    power_point = rs.getInt("power_point");
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }

        return power_point;
    }

    public static List<String> getAllCityUUIDs() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        List<String> uuidList = new ArrayList<>();

        String query = "SELECT uuid FROM city";
        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                uuidList.add(uuid);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        return uuidList;
    }

    public static void uncachePlayer(UUID uuid) {
        playerCities.remove(uuid);
    }
}
