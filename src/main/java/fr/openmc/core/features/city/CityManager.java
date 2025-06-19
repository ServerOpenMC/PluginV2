package fr.openmc.core.features.city;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sk89q.worldedit.math.BlockVector2;
import fr.openmc.api.chronometer.Chronometer;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.commands.*;
import fr.openmc.core.features.city.events.CityDeleteEvent;
import fr.openmc.core.features.city.listeners.CityChatListener;
import fr.openmc.core.features.city.models.*;
import fr.openmc.core.features.city.sub.bank.CityBankManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.NPCManager;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.utils.CacheOfflinePlayer;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CityManager implements Listener {
 
    @Getter
    private static final HashMap<String, City> citiesByName = new HashMap<>();
    @Getter
    private static final HashMap<String, City> citiesByUUID = new HashMap<>();
    private static final HashMap<UUID, City> playerCities = new HashMap<>();
    public static final HashMap<BlockVector2, City> claimedChunks = new HashMap<>();
    public static HashMap<String, Integer> freeClaim = new HashMap<>();
    private static HashMap<City, City> requestedAllies = new HashMap<>();
    private static HashMap<String, City> cities = new HashMap<>();
    private static HashMap<BlockVector2, City> claimedChunks = new HashMap<>();

    public CityManager() {
        OMCPlugin.registerEvents(this);

        loadCities();

        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("city_members", ((args, sender, command) -> {
            String playerCity = playerCities.get(sender.getUniqueId()).getUUID();

            if (playerCity == null)
                return List.of();

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
                new CityChestCommand()
        );

        OMCPlugin.registerEvents(
                new CityChatListener()
            );

        new ProtectionsManager();
        // SUB-FEATURE
        new MayorManager();
        new MascotsManager();
        new WarManager();
        new CityBankManager();
    }

    private static Dao<DBCity, String> citiesDao;
    private static Dao<DBCityMember, String> membersDao;
    private static Dao<DBCityPermission, String> permissionsDao;
    private static Dao<DBCityClaim, String> claimsDao;
    private static Dao<DBCityChest, String> chestsDao;

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBCity.class);
        citiesDao = DaoManager.createDao(connectionSource, DBCity.class);

        TableUtils.createTableIfNotExists(connectionSource, DBCityMember.class);
        membersDao = DaoManager.createDao(connectionSource, DBCityMember.class);

        TableUtils.createTableIfNotExists(connectionSource, DBCityPermission.class);
        permissionsDao = DaoManager.createDao(connectionSource, DBCityPermission.class);

        TableUtils.createTableIfNotExists(connectionSource, DBCityClaim.class);
        claimsDao = DaoManager.createDao(connectionSource, DBCityClaim.class);

        TableUtils.createTableIfNotExists(connectionSource, DBCityChest.class);
        chestsDao = DaoManager.createDao(connectionSource, DBCityChest.class);
    }

    // ==================== Database Methods ====================

    private static void loadCities() {
        try {
            cities.clear();
            citiesDao.queryForAll().forEach(city -> cities.put(city.getUUID(), city.deserialize()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            playerCities.clear();
            membersDao.queryForAll().forEach(member -> playerCities.put(member.getPlayer(), getCity(member.getCity())));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            claimedChunks.clear();
            claimsDao.queryForAll()
                    .forEach(claim -> claimedChunks.put(claim.getBlockVector(), getCity(claim.getCity())));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveCity(City city) {
        try {
            citiesDao.createOrUpdate(city.serialize());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will add a player to a city in the database
     *
     * @param city   The city to add the player to
     * @param player The player to add to the city
     */
    public static void addPlayerToCity(City city, UUID player) {
        playerCities.put(player, city);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                membersDao.create(new DBCityMember(player, city.getUUID()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Will remove a player from a city in the database
     *
     * @param city   The city to remove the player from
     * @param player The player to remove from the city
     */
    public static void removePlayerFromCity(City city, UUID player) {
        playerCities.remove(player);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                membersDao.delete(new DBCityMember(player, city.getUUID()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static HashMap<UUID, Set<CPermission>> getCityPermissions(City city) {
        HashMap<UUID, Set<CPermission>> permissions = new HashMap<>();

        try {
            QueryBuilder<DBCityPermission, String> query = permissionsDao.queryBuilder();
            query.where().eq("city", city.getUUID());
            List<DBCityPermission> dbPermissions = permissionsDao.query(query.prepare());

            dbPermissions.forEach(dbPermission -> {
                Set<CPermission> playerPermissions = permissions.getOrDefault(dbPermission.getPlayer(),
                        new HashSet<>());
                playerPermissions.add(dbPermission.getPermission());
                permissions.put(dbPermission.getPlayer(), playerPermissions);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return permissions;
    }
    public static void addPlayerPermission(City city, UUID player, CPermission permission) {
        try {
            permissionsDao.create(new DBCityPermission(city.getUUID(), player, permission.name()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removePlayerPermission(City city, UUID player, CPermission permission) {
        try {
            permissionsDao.delete(new DBCityPermission(city.getUUID(), player, permission.name()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<Integer, ItemStack[]> getCityChestContent(City city) {
        HashMap<Integer, ItemStack[]> pages = new HashMap<>();

        try {
            QueryBuilder<DBCityChest, String> query = chestsDao.queryBuilder();
            query.where().eq("city", city.getUUID());
            List<DBCityChest> dbChestPages = chestsDao.query(query.prepare());

            dbChestPages.forEach(page -> pages.put(page.getPage(), page.getContent()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pages;
    }

    public static void saveChestPage(City city, int page, ItemStack[] content) {
        try {
            DeleteBuilder<DBCityChest, String> delete = chestsDao.deleteBuilder();
            delete.where().eq("city", city.getUUID()).and().eq("page", page);
            chestsDao.delete(delete.prepare());

            chestsDao.create(new DBCityChest(city.getUUID(), page, content));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void claimChunk(City city, BlockVector2 chunk) {
        claimedChunks.put(chunk, city);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                claimsDao.create(new DBCityClaim(chunk, city.getUUID()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void unclaimChunk(City city, BlockVector2 chunk) {
        claimedChunks.remove(chunk);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                claimsDao.delete(new DBCityClaim(chunk, city.getUUID()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // ==================== General helper methods ====================

    /**
     * Get all cities registered in manager
     *
     * @return cities
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
        List<String> uuidList = new ArrayList<>();
        cities.forEach((name, city) -> uuidList.add(city.getUUID()));
        return uuidList;
    }

    /**
     * Check if a chunk is claimed
     *
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
     *
     * @param city The UUID of the city
     * @return The city object, or null if not found
     */
    public static City getCity(String city) {
        return cities.get(city);
    }

    /**
     * Get a cities claimed chunks
     *
     * @param inCity The cities whose chunks are requested
     * @return The cities claimed chunks
     */
    public static Set<BlockVector2> getCityChunks(City inCity) {
        Set<BlockVector2> chunks = new HashSet<>();

        claimedChunks.forEach((chunk, city) -> {
            if (city.getUUID() == inCity.getUUID())
                chunks.add(chunk);
        });

        return chunks;
    }

    /**
     * Get a cities members
     *
     * @param inCity The cities whose members are requested
     * @return The cities members
     */
    public static Set<UUID> getCityMembers(City inCity) {
        Set<UUID> members = new HashSet<>();

        playerCities.forEach((player, city) -> {
            if (city.getUUID() == inCity.getUUID())
                members.add(player);
        });

        return members;
    }

    /**
     * Get a city by its member
     *
     * @param player The UUID of the member
     * @return The city object, or null if not found
     */
    public static City getPlayerCity(UUID player) {
        return playerCities.get(player);
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
        return claimedChunks.get(BlockVector2.at(x, z));
    }

    /**
     * Register a city
     *
     * @param city The city object
     */
    public static void registerCity(City city) {
        citiesByUUID.put(city.getUUID(), city);
        citiesByName.put(city.getName(), city);
    }

    /**
     * Delete a city
     *
     * @param city The city
     */
    public static void deleteCity(City city) {
        MayorManager.cityMayor.remove(city.getUUID());
        MayorManager.cityElections.remove(city.getUUID());
        MayorManager.playerVote.remove(city.getUUID());

        List<UUID> membersCopy = new ArrayList<>(city.getMembers());
        for (UUID memberId : membersCopy) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null)
                city.removePlayer(memberId);

            member = CacheOfflinePlayer.getOfflinePlayer(memberId).getPlayer();
            if (member == null)
                continue;

            if (Chronometer.containsChronometer(memberId, "Mascot:chest"))
                if (Bukkit.getEntity(memberId) != null)
                    Chronometer.stopChronometer(member, "Mascot:chest", null, "%null%");

            Mascot mascot = city.getMascot();
            if (mascot == null)
                continue;

            if (!DynamicCooldownManager.isReady(mascot.getMascotUUID().toString(), "mascots:move")) {
                if (Bukkit.getEntity(memberId) != null) {
                    DynamicCooldownManager.clear(mascot.getMascotUUID().toString(), "mascots:move");
                }
            }
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
            if (playerCity != null && playerCity.equals(city)) {
                playerIterator.remove();
            }
        }

        if (DynamicCooldownManager.isReady(city.getUUID(), "city:type")) {
            DynamicCooldownManager.clear(city.getUUID(), "city:type");
        }

        MascotsManager.removeMascotsFromCity(city);
        NPCManager.removeNPCS(city.getUUID());

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                citiesDao.delete(city.serialize());

                DeleteBuilder<DBCityMember, String> membersDelete = membersDao.deleteBuilder();
                membersDelete.where().eq("city", city.getUUID());
                membersDao.delete(membersDelete.prepare());

                DeleteBuilder<DBCityPermission, String> permissionsDelete = permissionsDao.deleteBuilder();
                permissionsDelete.where().eq("city", city.getUUID());
                permissionsDao.delete(permissionsDelete.prepare());

                DeleteBuilder<DBCityClaim, String> claimsDelete = claimsDao.deleteBuilder();
                claimsDelete.where().eq("city", city.getUUID());
                claimsDao.delete(claimsDelete.prepare());

                DeleteBuilder<DBCityChest, String> chestsDelete = chestsDao.deleteBuilder();
                chestsDelete.where().eq("city", city.getUUID());
                chestsDao.delete(chestsDelete.prepare());

                MayorManager.removeCity(city);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new CityDeleteEvent(city));
        });
    }
    
    public static void requestAllie(City resquesterCity, City targetCity) {
        requestedAllies.put(resquesterCity, targetCity);
	    for (UUID m : targetCity.getMembers()) {
		    if (targetCity.hasPermission(m, CPermission.OWNER) || targetCity.hasPermission(m, CPermission.MANAGE_ALLIANCE)) {
			    Player player = Bukkit.getServer().getPlayer(m);
			    if (player == null) continue;
			    if (player.isOnline()) {
				    MessagesManager.sendMessage(player, Component.text("La ville " + resquesterCity.getName() + " vous demande en allié !"), Prefix.CITY, MessageType.INFO, true);
			    }
		    }
	    }
    }
}
