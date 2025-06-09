package fr.openmc.core.features.city;

import com.sk89q.worldedit.math.BlockVector2;
import fr.openmc.api.chronometer.Chronometer;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.commands.*;
import fr.openmc.core.features.city.events.ChunkClaimedEvent;
import fr.openmc.core.features.city.events.CityCreationEvent;
import fr.openmc.core.features.city.listeners.CityChatListener;
import fr.openmc.core.features.city.sub.bank.CityBankManager;
import fr.openmc.core.features.city.sub.mascots.Mascot;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mayor.commands.AdminMayorCommands;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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
    public static HashMap<String, Integer> freeClaim = new HashMap<>();

    public CityManager() {
        OMCPlugin.registerEvents(this);

        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("city_members", ((args, sender, command) -> {
            String playerCity = playerCities.get(sender.getUniqueId()).getUUID();

            if (playerCity == null) return List.of();

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                try {
                    PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid, x, z FROM city_regions");
                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        claimedChunks.put(BlockVector2.at(rs.getInt("x"), rs.getInt("z")), getCity(rs.getString("city_uuid")));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            return playerCities.keySet().stream()
                    .filter(uuid -> playerCities.get(uuid).getUUID().equals(playerCity))
                    .map(uuid -> CacheOfflinePlayer.getOfflinePlayer(uuid).getName())
                    .collect(Collectors.toList());
        }));

        CommandsManager.getHandler().register(
                new CityCommands(),
                new AdminCityCommands(),
                new CityPermsCommands(),
                new CityChatCommand(),
                new CityChestCommand(),
                new AdminMayorCommands()
        );

        OMCPlugin.registerEvents(
                new CityChatListener()
        );
        new ProtectionsManager();

        // SUB-FEATURE
        new MascotsManager();
        new MayorManager();
        new WarManager();
        new CityBankManager();

        freeClaim = loadFreeClaims();
    }

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city (uuid VARCHAR(8) NOT NULL PRIMARY KEY, owner VARCHAR(36) NOT NULL, name VARCHAR(32), balance DOUBLE DEFAULT 0, type VARCHAR(8) NOT NULL);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_members (city_uuid VARCHAR(8) NOT NULL, player VARCHAR(36) NOT NULL PRIMARY KEY);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_permissions (city_uuid VARCHAR(8) NOT NULL, player VARCHAR(36) NOT NULL, permission VARCHAR(255) NOT NULL);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_chests (city_uuid VARCHAR(8) NOT NULL, page TINYINT NOT NULL, content LONGBLOB);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_regions (city_uuid VARCHAR(8) NOT NULL, x MEDIUMINT NOT NULL, z MEDIUMINT NOT NULL);").executeUpdate();// Il faut esperer qu'aucun clodo n'ira à 134.217.712 blocks du spawn
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS city_power (city_uuid VARCHAR(8) NOT NULL, power_point INT NOT NULL);").executeUpdate();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS free_claim (city_uuid VARCHAR(8) NOT NULL PRIMARY KEY, claim INT NOT NULL);").executeUpdate();
    }

    @EventHandler
    public void onChunkClaim(ChunkClaimedEvent event) {
        claimedChunks.put(BlockVector2.at(event.getChunk().getX(), event.getChunk().getZ()), event.getCity());
    }

    /**
     * Get all City
     *
     * @return A collection of all cities
     */
    public static Collection<City> getCities() {
        return cities.values();
    }

    /**
     * Get all UUIDs of cities
     *
     * @return A list of all city UUIDs
     */
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
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return uuidList;
    }

    /**
     * Get all free claims
     *
     * @return A map of city UUIDs and their claim
     */
    public static HashMap<String, Integer> loadFreeClaims() {
        HashMap<String, Integer> freeClaims = new HashMap<>();

        String query = "SELECT city_uuid, claim FROM free_claim";
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String cityUuid = rs.getString("city_uuid");
                int claim = rs.getInt("claim");
                if (claim > 0) {
                    freeClaims.put(cityUuid, claim);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return freeClaims;
    }

    /**
     * Save free claims to the database
     * @param freeClaims A map of city UUIDs and their claim
     */
    public static void saveFreeClaims(HashMap<String, Integer> freeClaims){
        String query;

        if (OMCPlugin.isUnitTestVersion()) {
            query = "MERGE INTO free_claim KEY(city_uuid) VALUES (?, ?)";
        } else {
            query = "INSERT INTO free_claim (city_uuid, claim) VALUES (?, ?) ON DUPLICATE KEY UPDATE claim = ?";
        }
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(query)) {
            for (Map.Entry<String, Integer> entry : freeClaims.entrySet()) {
                if (entry.getValue() > 0) {
                    statement.setString(1, entry.getKey());
                    statement.setInt(2, entry.getValue());
                    statement.setInt(3, entry.getValue());
                    statement.addBatch();
                } else {
                    try (PreparedStatement deleteStatement = DatabaseManager.getConnection().prepareStatement(
                            "DELETE FROM free_claim WHERE city_uuid = ?")) {
                        deleteStatement.setString(1, entry.getKey());
                        deleteStatement.executeUpdate();
                    }
                }

            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if a chunk is claimed
     * @param x The x coordinate of the chunk
     * @param z The z coordinate of the chunk
     * @return true if the chunk is claimed, false otherwise
     */
    public static boolean isChunkClaimed(int x, int z) {
        return getCityFromChunk(x, z) != null;
    }

    /**
     * Check if a chunk is claimed in radius
     *
     * @param chunk  The chunk
     * @param radius The radius
     * @return true if the chunk is claimed, false otherwise
     */
    public static boolean isChunkClaimedInRadius(Chunk chunk, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (CityManager.isChunkClaimed(chunk.getX() + x, chunk.getZ() + z)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get a city by its UUID
     * @param cityUUID The UUID of the city
     * @return The city object, or null if not found
     */
    public static City getCity(String cityUUID) {
        if (!cities.containsKey(cityUUID)) {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT uuid FROM city WHERE uuid = ? LIMIT 1");
                statement.setString(1, cityUUID);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    City c = new City(cityUUID);
                    cities.put(c.getUUID(), c);
                    return c;
                }

                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return cities.get(cityUUID);
    }

    /**
     * Get a city by its member
     * @param playerUUID The UUID of the member
     * @return The city object, or null if not found
     */
    public static City getPlayerCity(UUID playerUUID) {
        if (!playerCities.containsKey(playerUUID)) {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT city_uuid FROM city_members WHERE player = ? LIMIT 1");
                statement.setString(1, playerUUID.toString());
                ResultSet rs = statement.executeQuery();

                if (!rs.next()) {
                    return null;
                }

                String city = rs.getString(1);
                cachePlayer(playerUUID, getCity(city));
                return getCity(city);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return playerCities.get(playerUUID);
    }

    /**
     * Get a city from a chunk
     *
     * @param x The x coordinate of the chunk
     * @param z The z coordinate of the chunk
     * @return The city object, or null if not found
     */
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

    /**
     * Cache a player with its city
     *
     * @param playerUUID The UUID of the player
     * @param city The city object
     */
    public static void cachePlayer(UUID playerUUID, City city) {
        playerCities.put(playerUUID, city);
    }

    /**
     * Uncache a player with its city
     *
     * @param playerUUID The UUID of the player
     */
    public static void uncachePlayer(UUID playerUUID) {
        playerCities.remove(playerUUID);
    }

    /**
     * Create a new city
     *
     * @param owner    The owner of the city
     * @param cityUUID The UUID of the city
     * @param name     The name of the city
     * @param type     The type of the city
     * @return The created city object
     */
    public static City createCity(Player owner, String cityUUID, String name, CityType type) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city VALUE (?, ?, ?, 0, ?)");
                statement.setString(1, cityUUID);
                statement.setString(2, owner.getUniqueId().toString());
                statement.setString(3, name);
                statement.setString(4, type == CityType.PEACE ? "peace" : "war");
                statement.executeUpdate();

                statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_chests VALUE (?, 1, null)");
                statement.setString(1, cityUUID);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        City city = new City(cityUUID);
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new CityCreationEvent(city, owner));
        });
        return city;
    }

    /**
     * Register a city
     *
     * @param city The city object
     */
    public static void registerCity(City city) {
        cities.put(city.getUUID(), city);
    }

    /**
     * Delete a city
     *
     * @param cityUUID The UUID of the city
     */
    public static void forgetCity(String cityUUID) {
        try {
            City city = cities.remove(cityUUID);
            if (city == null) return;

            MayorManager mayorManager = MayorManager.getInstance();
            mayorManager.cityMayor.remove(city);
            mayorManager.cityElections.remove(city);
            mayorManager.playerVote.remove(city);

            List<UUID> membersCopy = new ArrayList<>(city.getMembers());
            for (UUID members : membersCopy) {
                Player member = Bukkit.getPlayer(members);
                if (member == null) {
                    member = CacheOfflinePlayer.getOfflinePlayer(members).getPlayer();
                    if (member == null) {
                        continue;
                    }

                    if (Chronometer.containsChronometer(members, "Mascot:chest")) {
                        if (Bukkit.getEntity(members) != null) {
                            Chronometer.stopChronometer(member, "Mascot:chest", null, "%null%");
                        }
                    }

                    Mascot mascot = city.getMascot();
                    if (mascot != null) {

                        if (!DynamicCooldownManager.isReady(mascot.getMascotUUID().toString(), "mascots:move")) {
                            if (Bukkit.getEntity(members) != null) {
                                DynamicCooldownManager.clear(mascot.getMascotUUID().toString(), "mascots:move");
                            }
                        }
                    }
                }
                city.removePlayer(members);
            }

            Iterator<BlockVector2> iterator = claimedChunks.keySet().iterator();
            while (iterator.hasNext()) {
                BlockVector2 vector = iterator.next();
                City claimedCity = claimedChunks.get(vector);
                if (claimedCity != null && claimedCity.equals(city)) {
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

            if (DynamicCooldownManager.isReady(cityUUID, "city:type")) {
                DynamicCooldownManager.clear(cityUUID, "city:type");
            }

            freeClaim.remove(cityUUID);

            MascotsManager.removeMascotsFromCity(city);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
