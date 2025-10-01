package fr.openmc.core.features.city;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
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
import fr.openmc.core.features.city.sub.milestone.CityMilestoneManager;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.rank.CityRankCommands;
import fr.openmc.core.features.city.sub.rank.CityRankManager;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.city.view.CityViewManager;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.ChunkPos;
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
    private static final Map<UUID, City> cities = new HashMap<>();
    public static final Map<String, City> citiesByName = new HashMap<>();
    private static final Map<UUID, City> playerCities = new HashMap<>();
    private static final Map<ChunkPos, City> claimedChunks = new HashMap<>();

    public CityManager() {
        OMCPlugin.registerEvents(this);

        loadCities();

        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("city_members", ((args, sender, command) -> {
            UUID playerCityUUID = playerCities.get(sender.getUniqueId()).getUniqueId();

            if (playerCityUUID == null)
                return List.of();

            return playerCities.keySet().stream()
                    .filter(uuid -> playerCities.get(uuid).getUniqueId().equals(playerCityUUID))
                    .map(uuid -> CacheOfflinePlayer.getOfflinePlayer(uuid).getName())
                    .collect(Collectors.toList());
        })).registerSuggestion("city_ranks", ((args, sender, command) -> {
                    City city = playerCities.get(sender.getUniqueId());
                    if (city == null) return List.of();

                    return city.getRanks().stream()
                            .map(DBCityRank::getName)
                            .collect(Collectors.toList());
                })
        );

        CommandsManager.getHandler().register(
                new AdminCityCommands(),
                new CityCommands(),
                new CityChatCommand(),
                new CityPermsCommands(),
                new CityChestCommand(),
                new CityRankCommands(),
                new CityTopCommands()
        );

        OMCPlugin.registerEvents(
                new CityChatListener()
        );

        // SUB-FEATURE
        new MayorManager();
        new ProtectionsManager();
        new WarManager();
        new CityBankManager();
        new CityStatisticsManager();
        new NotationManager();
        new CityRankManager();
        new CityMilestoneManager();
    }

    private static Dao<DBCity, String> citiesDao;
    private static Dao<DBCityMember, String> membersDao;
    private static Dao<DBCityPermission, String> permissionsDao;
    private static Dao<DBCityClaim, String> claimsDao;
    private static Dao<DBCityChest, String> chestsDao;

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
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
            for (DBCity dbCity : citiesDao.queryForAll()) {
                cities.put(dbCity.getUniqueId(), dbCity.deserialize());
            }

            citiesByName.clear();
            for (DBCity dbCity : citiesDao.queryForAll()) {
                City city = getCity(dbCity.getUniqueId());
                if (city != null) citiesByName.put(city.getName(), city);
            }

            playerCities.clear();
            for (DBCityMember member : membersDao.queryForAll()) {
                City city = getCity(member.getCityUUID());
                if (city != null) playerCities.put(member.getPlayerUUID(), city);
            }

            claimedChunks.clear();
            for (DBCityClaim claim : claimsDao.queryForAll()) {
                City city = getCity(claim.getCityUUID());
                if (city != null) claimedChunks.put(claim.getChunkPos(), city);
            }

            cities.values().forEach(City::initializeRanks);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur du chargements des Villes ", e);
        }
    }

    public static void saveCity(City city) {
        try {
            citiesDao.createOrUpdate(city.serialize());
        } catch (SQLException e) {
            throw new RuntimeException("Erreur des sauvegardes des villes ", e);
        }
    }

    /**
     * Will add a player to a city in the database
     *
     * @param city   The city to add the player to
     * @param playerUUID The playerUUID to add to the city
     */
    public static void addPlayerToCity(City city, UUID playerUUID) {
        if (city == null || playerUUID == null) return;

        playerCities.put(playerUUID, city);
        CityViewManager.updateView(playerUUID);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                membersDao.create(new DBCityMember(playerUUID, city.getUniqueId()));
            } catch (SQLException e) {
                throw new RuntimeException("Erreur d'ajout de membre dans une ville '", e);
            }
        });
    }

    /**
     * Will remove a player from a city in the database
     *
     * @param city   The city to remove the player from
     * @param playerUUID The playerUUID to remove from the city
     */
    public static void removePlayerFromCity(City city, UUID playerUUID) {
        if (city == null || playerUUID == null) return;

        playerCities.remove(playerUUID);
        CityViewManager.updateView(playerUUID);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                membersDao.delete(new DBCityMember(playerUUID, city.getUniqueId()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static HashMap<UUID, Set<CityPermission>> getCityPermissions(City city) {
        HashMap<UUID, Set<CityPermission>> permissions = new HashMap<>();

        try {
            QueryBuilder<DBCityPermission, String> query = permissionsDao.queryBuilder();
            query.where().eq("city_uuid", city.getUniqueId());
            List<DBCityPermission> dbPermissions = permissionsDao.query(query.prepare());

            dbPermissions.forEach(dbPermission -> {
                Set<CityPermission> playerPermissions = permissions.getOrDefault(dbPermission.getPlayer(),
                        new HashSet<>());
                playerPermissions.add(dbPermission.getPermission());
                permissions.put(dbPermission.getPlayer(), playerPermissions);
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return permissions;
    }

    public static void addPlayerPermission(City city, UUID playerUUID, CityPermission permission) {
        try {
            permissionsDao.create(new DBCityPermission(city.getUniqueId(), playerUUID, permission.name()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removePlayerPermission(City city, UUID playerUUID, CityPermission permission) {
        try {
            DeleteBuilder<DBCityPermission, String> delete = permissionsDao.deleteBuilder();
            delete.where()
                    .eq("city_uuid", city.getUniqueId())
                    .and()
                    .eq("player", playerUUID)
                    .and()
                    .eq("permission", permission.name());
            permissionsDao.delete(delete.prepare());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Integer, ItemStack[]> getCityChestContent(City city) {
        HashMap<Integer, ItemStack[]> pages = new HashMap<>();

        try {
            QueryBuilder<DBCityChest, String> query = chestsDao.queryBuilder();
            query.where().eq("city_uuid", city.getUniqueId());
            List<DBCityChest> dbChestPages = chestsDao.query(query.prepare());

            dbChestPages.forEach(page -> pages.put(page.getPage(), page.getContent()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return pages;
    }

    public static void saveChestPage(City city, int page, ItemStack[] content) {
        try {
            DeleteBuilder<DBCityChest, String> delete = chestsDao.deleteBuilder();
            delete.where().eq("city_uuid", city.getUniqueId()).and().eq("page", page);
            chestsDao.delete(delete.prepare());

            chestsDao.create(new DBCityChest(city.getUniqueId(), page, content));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void claimChunk(City city, ChunkPos chunkPos) {
        claimedChunks.put(chunkPos, city);
        CityViewManager.updateAllViews();

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                claimsDao.create(new DBCityClaim(chunkPos, city.getUniqueId()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void unclaimChunk(City city, ChunkPos chunkPos) {
        claimedChunks.remove(chunkPos);
        CityViewManager.updateAllViews();

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                DeleteBuilder<DBCityClaim, String> delete = claimsDao.deleteBuilder();
                delete.where().eq("city_uuid", city.getUniqueId())
                        .and().eq("x", chunkPos.x())
                        .and().eq("z", chunkPos.z());

                claimsDao.delete(delete.prepare());
            } catch (SQLException e) {
                throw new RuntimeException(e);
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
    public static List<UUID> getAllCityUUIDs() {
        List<UUID> uuidList = new ArrayList<>();
        cities.forEach((name, city) -> uuidList.add(city.getUniqueId()));
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
     * @param cityUUID The {@link UUID} of the city
     * @return The {@link City}, or null if not found
     */
    public static City getCity(UUID cityUUID) {
        return cities.get(cityUUID);
    }

    /**
     * Get a city by its name
     *
     * @param name The name of the city
     * @return The city object, or null if not found
     */
    public static City getCityByName(String name) {
        return citiesByName.get(name);
    }

    /**
     * Get a cities claimed chunks
     *
     * @param inCity The cities whose chunks are requested
     * @return The cities claimed chunks
     */
    public static Set<ChunkPos> getCityChunks(City inCity) {
        Set<ChunkPos> chunks = new HashSet<>();

        claimedChunks.forEach((chunk, city) -> {
            if (city == null) return;
            if (city.getUniqueId().equals(inCity.getUniqueId()))
                chunks.add(chunk);
        });

        return chunks;
    }

    /**
     * Get a city member
     *
     * @param inCity The cities whose members are requested
     * @return The city members
     */
    public static Set<UUID> getCityMembers(City inCity) {
        Set<UUID> members = new HashSet<>();

        playerCities.forEach((player, city) -> {
            if (city.getUniqueId().equals(inCity.getUniqueId()))
                members.add(player);
        });

        return members;
    }

    /**
     * Get a city by its member
     *
     * @param playerUUID The UUID of the member
     * @return The city object, or null if not found
     */
    public static City getPlayerCity(UUID playerUUID) {
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
        return claimedChunks.get(new ChunkPos(x, z));
    }

    /**
     * Get a city from a chunk
     *
     * @param chunkPos The chunk position
     * @return The city object, or null if not found
     */
    @Nullable
    public static City getCityFromChunk(ChunkPos chunkPos) {
        return claimedChunks.get(chunkPos);
    }

    /**
     * Register a city
     *
     * @param city The city object
     */
    public static void registerCity(City city) {
        cities.put(city.getUniqueId(), city);
        citiesByName.put(city.getName(), city);
    }

    /**
     * Delete a city
     *
     * @param city The city
     */
    public static void deleteCity(City city) {
        if (city == null) return;

        MayorManager.cityMayor.remove(city.getUniqueId());
        MayorManager.cityElections.remove(city.getUniqueId());
        MayorManager.playerVote.remove(city.getUniqueId());

        List<UUID> membersCopy = new ArrayList<>(city.getMembers());
        for (UUID memberId : membersCopy) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null)
                city.removePlayer(memberId);

            member = CacheOfflinePlayer.getOfflinePlayer(memberId).getPlayer();
            if (member == null)
                continue;

            if (Chronometer.containsChronometer(memberId, "mascot:stick"))
                if (Bukkit.getEntity(memberId) != null)
                    Chronometer.stopChronometer(member, "mascot:stick", null, "%null%");

            Mascot mascot = city.getMascot();
            if (mascot == null)
                continue;

            if (!DynamicCooldownManager.isReady(mascot.getMascotUUID(), "mascots:move")) {
                if (Bukkit.getEntity(memberId) != null) {
                    DynamicCooldownManager.clear(mascot.getMascotUUID(), "mascots:move", false);
                }
            }
        }

        Iterator<ChunkPos> iterator = claimedChunks.keySet().iterator();
        while (iterator.hasNext()) {
            ChunkPos chunkPos = iterator.next();
            City claimedCity = claimedChunks.get(chunkPos);
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

        if (DynamicCooldownManager.isReady(city.getUniqueId(), "city:type")) {
            DynamicCooldownManager.clear(city.getUniqueId(), "city:type", false);
        }

        MascotsManager.removeMascotsFromCity(city);
        NPCManager.removeNPCS(city.getUniqueId());

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                citiesDao.delete(city.serialize());

                DeleteBuilder<DBCityMember, String> membersDelete = membersDao.deleteBuilder();
                membersDelete.where().eq("city_uuid", city.getUniqueId());
                membersDao.delete(membersDelete.prepare());

                DeleteBuilder<DBCityPermission, String> permissionsDelete = permissionsDao.deleteBuilder();
                permissionsDelete.where().eq("city_uuid", city.getUniqueId());
                permissionsDao.delete(permissionsDelete.prepare());

                CityRankManager.removeRanks(city);

                DeleteBuilder<DBCityClaim, String> claimsDelete = claimsDao.deleteBuilder();
                claimsDelete.where().eq("city_uuid", city.getUniqueId());
                claimsDao.delete(claimsDelete.prepare());

                DeleteBuilder<DBCityChest, String> chestsDelete = chestsDao.deleteBuilder();
                chestsDelete.where().eq("city_uuid", city.getUniqueId());
                chestsDao.delete(chestsDelete.prepare());

                MayorManager.removeCity(city);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        cities.remove(city.getUniqueId());
        citiesByName.remove(city.getName());

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new CityDeleteEvent(city));
        });

        CityViewManager.updateAllViews();
    }
}
