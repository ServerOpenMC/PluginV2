package fr.openmc.core.features.dream;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.dream.commands.AdminDreamCommands;
import fr.openmc.core.features.dream.commands.DreamCommands;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.generation.listeners.CloudStructureDispenserListener;
import fr.openmc.core.features.dream.generation.listeners.ReplaceBlockListener;
import fr.openmc.core.features.dream.generation.structures.DreamStructuresManager;
import fr.openmc.core.features.dream.listeners.armors.DreamArmorImplListener;
import fr.openmc.core.features.dream.listeners.biomes.PlayerEnteredBiome;
import fr.openmc.core.features.dream.listeners.dream.*;
import fr.openmc.core.features.dream.listeners.orb.PlayerObtainOrb;
import fr.openmc.core.features.dream.listeners.others.CraftingConvertorListener;
import fr.openmc.core.features.dream.listeners.others.PlayerEatSomnifere;
import fr.openmc.core.features.dream.listeners.others.SingularityCraftListener;
import fr.openmc.core.features.dream.listeners.registry.DreamItemEquipListener;
import fr.openmc.core.features.dream.mecanism.cloudfishing.CloudFishingManager;
import fr.openmc.core.features.dream.mecanism.cold.ColdManager;
import fr.openmc.core.features.dream.mecanism.metaldetector.MetalDetectorManager;
import fr.openmc.core.features.dream.mecanism.singularity.SingularityManager;
import fr.openmc.core.features.dream.mecanism.tradernpc.GlaciteNpcManager;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.features.dream.models.db.DBPlayerSave;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.*;
import fr.openmc.core.utils.LocationUtils;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class DreamManager {
    // ** CONSTANTS **
    public static final Long BASE_DREAM_TIME = 300L;

    private static final HashMap<UUID, DBPlayerSave> playerSaveData = new HashMap<>();

    private static final HashMap<UUID, DreamPlayer> dreamPlayerData = new HashMap<>();
    public static final HashMap<UUID, DBDreamPlayer> cacheDreamPlayer = new HashMap<>();

    private static Dao<DBDreamPlayer, String> dreamPlayerDao;
    private static Dao<DBPlayerSave, String> savePlayerDao;

    public static void init() {
        // ** LISTENERS **
        OMCPlugin.registerEvents(
                new PlayerChangeWorldListener(),
                new PlayerJoinListener(),
                new PlayerQuitListener(),
                new PlayerDeathListener(),
                new PlayerCommandListener(),
                new PlayerDreamTimeEndListener(),
                new PlayerSleepListener(),
                new PlayerEnteredBiome(),
                new PlayerObtainOrb(),
                new PlayerDamageListener(),
                new ReplaceBlockListener(),
                new PlayerEatSomnifere(),
                new CloudStructureDispenserListener(),
                new CraftingConvertorListener(),
                new DreamItemEquipListener(),
                new DreamArmorImplListener(),
                new SingularityCraftListener()
        );

        // ** MANAGERS **
        DreamEnchantementRegistry.init();
        DreamDimensionManager.init();
        GlaciteNpcManager.init();
        DreamStructuresManager.init();
        DreamItemRegistry.init();
        DreamBlocksRegistry.init();
        DreamMobsRegistry.init();
        DreamLootTableRegistry.init();
        DreamBlocksDropsRegistry.init();
        CloudFishingManager.init();
        MetalDetectorManager.init();
        ColdManager.init();
        SingularityManager.init();

        // ** COMMANDS **
        CommandsManager.getHandler().register(
                new AdminDreamCommands(),
                new DreamCommands()
        );

        // ** LOAD DATAS **
        loadAllDreamPlayerData();
        loadAllPlayerSaveData();
    }

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBDreamPlayer.class);
        dreamPlayerDao = DaoManager.createDao(connectionSource, DBDreamPlayer.class);

        TableUtils.createTableIfNotExists(connectionSource, DBPlayerSave.class);
        savePlayerDao = DaoManager.createDao(connectionSource, DBPlayerSave.class);

        SingularityManager.initDB(connectionSource);
    }

    public static void disable() {
        DreamManager.saveAllPlayerSaveData();
        DreamManager.saveAllDreamPlayerData();

        SingularityManager.disable();
    }

    private static void loadAllPlayerSaveData() {
        try {
            playerSaveData.clear();
            savePlayerDao.queryForAll().forEach(playerData -> {
                playerSaveData.put(playerData.getPlayerUUID(), playerData);
                try {
                    savePlayerDao.delete(playerData);
                } catch (SQLException e) {
                    OMCPlugin.getInstance().getSLF4JLogger().error("Cannot load player save data", e);
                }
            });
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Cannot load player save data", e);
        }
    }

    public static void saveAllPlayerSaveData() {
        playerSaveData.forEach((uuid, playerSave) -> {
            try {
                savePlayerDao.createOrUpdate(playerSave);
            } catch (SQLException e) {
                OMCPlugin.getInstance().getSLF4JLogger().error("Cannot save player save data for player {}", uuid, e);
            }
        });
    }


    private static void loadAllDreamPlayerData() {
        try {
            dreamPlayerData.clear();
            dreamPlayerDao.queryForAll().forEach(playerData ->
                    cacheDreamPlayer.put(playerData.getPlayerUUID(), playerData)
            );
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Cannot load dream player data", e);
        }
    }

    public static void saveAllDreamPlayerData() {
        cacheDreamPlayer.forEach((uuid, dbDreamPlayer) -> {
            try {
                dreamPlayerDao.createOrUpdate(dbDreamPlayer);
            } catch (SQLException e) {
                OMCPlugin.getInstance().getSLF4JLogger().error("Cannot save dream player data", e);
            }
        });
    }

    public static void saveDreamPlayerData(DreamPlayer dreamPlayer) {
        saveDreamPlayerData(dreamPlayer.save());
    }

    public static void saveDreamPlayerData(DBDreamPlayer dbDreamPlayer) {
        try {
            dreamPlayerDao.createOrUpdate(dbDreamPlayer);
            if (cacheDreamPlayer.containsKey(dbDreamPlayer.getPlayerUUID())) {
                cacheDreamPlayer.replace(dbDreamPlayer.getPlayerUUID(), dbDreamPlayer);
            } else {
                cacheDreamPlayer.put(dbDreamPlayer.getPlayerUUID(), dbDreamPlayer);
            }

        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Cannot save player save data", e);
        }
    }

    public static DBDreamPlayer getCacheDreamPlayer(Player player) {
        if (!cacheDreamPlayer.containsKey(player.getUniqueId())) return null;

        return cacheDreamPlayer.get(player.getUniqueId());
    }

    public static void addCacheDreamPlayer(Player player, DBDreamPlayer dbDreamPlayer) {
        if (cacheDreamPlayer.containsKey(player.getUniqueId())) return;

        cacheDreamPlayer.put(player.getUniqueId(), dbDreamPlayer);
    }

    public static DreamPlayer getDreamPlayer(Player player) {
        if (!dreamPlayerData.containsKey(player.getUniqueId())) return null;

        return dreamPlayerData.get(player.getUniqueId());
    }

    public static void addDreamPlayer(Player player, Location oldLocation) throws IOException {
        PlayerInventory playerInv = player.getInventory();

        ItemStack[] oldInv = playerInv.getContents().clone();

        DBDreamPlayer cacheDreamPlayer = getCacheDreamPlayer(player);
        if (cacheDreamPlayer == null || cacheDreamPlayer.getDreamInventory() == null) {
            player.getInventory().clear();
        } else {
            BukkitSerializer.playerInventoryFromBase64(playerInv, cacheDreamPlayer.getDreamInventory());
            player.updateInventory();
        }

        PlayerInventory dreamPlayerInv = player.getInventory();
        DreamPlayer newDreamPlayer = new DreamPlayer(player, oldInv, oldLocation, dreamPlayerInv);
        dreamPlayerData.put(player.getUniqueId(), newDreamPlayer);
        playerSaveData.put(player.getUniqueId(), newDreamPlayer.savePlayer());
    }

    public static void removeDreamPlayer(Player player, Location dreamLocation) {
        DreamPlayer dreamPlayer = dreamPlayerData.remove(player.getUniqueId());
        playerSaveData.remove(player.getUniqueId());

        if (dreamPlayer == null) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("Cannot remove player {}({}) from Dream", player.getName(), player.getUniqueId());
            return;
        }

        dreamPlayer.cancelTimeTask();
        dreamPlayer.cancelColdTask();

        ItemStack[] oldInventory = dreamPlayer.getOldInventory();
        PlayerInventory dreamInventory = player.getInventory();

        DBDreamPlayer cacheDreamPlayer = getCacheDreamPlayer(player);
        String serializedDreamInventory = BukkitSerializer.playerInventoryToBase64(dreamInventory);
        if (cacheDreamPlayer != null) {
            cacheDreamPlayer.setDreamInventory(serializedDreamInventory);
            cacheDreamPlayer.setDreamX(dreamLocation.getX());
            cacheDreamPlayer.setDreamY(dreamLocation.getY());
            cacheDreamPlayer.setDreamZ(dreamLocation.getZ());
        } else {
            addCacheDreamPlayer(player, new DBDreamPlayer(
                    player.getUniqueId(),
                    dreamPlayer.getMaxDreamTime(),
                    serializedDreamInventory,
                    dreamLocation.getX(),
                    dreamLocation.getY(),
                    dreamLocation.getZ(),
                    0
            ));
        }

        player.getInventory().setContents(oldInventory);
        player.updateInventory();
        saveDreamPlayerData(cacheDreamPlayer);
    }

    public static void preloadSavePlayer(Player player, Location dreamLocation) throws IOException {
        DBPlayerSave playerSave = playerSaveData.remove(player.getUniqueId());

        if (playerSave == null) {
            player.teleportAsync(SpawnManager.getSpawnLocation());
            OMCPlugin.getInstance().getSLF4JLogger().warn("Nothing to load from {}({})", player.getName(), player.getUniqueId());
            return;
        }
        PlayerInventory dreamInventory = player.getInventory();
        String serializedDreamInventory = BukkitSerializer.playerInventoryToBase64(dreamInventory);

        BukkitSerializer.playerInventoryFromBase64(dreamInventory, playerSave.getInventory());
        player.updateInventory();

        DBDreamPlayer cacheDreamPlayer = getCacheDreamPlayer(player);
        if (cacheDreamPlayer != null) {
            cacheDreamPlayer.setDreamInventory(serializedDreamInventory);
            cacheDreamPlayer.setDreamX(dreamLocation.getX());
            cacheDreamPlayer.setDreamY(dreamLocation.getY());
            cacheDreamPlayer.setDreamZ(dreamLocation.getZ());
        } else {
            addCacheDreamPlayer(player, new DBDreamPlayer(
                    player.getUniqueId(),
                    DreamManager.BASE_DREAM_TIME,
                    serializedDreamInventory,
                    dreamLocation.getX(),
                    dreamLocation.getY(),
                    dreamLocation.getZ(),
                    0
            ));
            cacheDreamPlayer = getCacheDreamPlayer(player);
        }

        saveDreamPlayerData(cacheDreamPlayer);

        World oldWorld = Bukkit.getWorld(playerSave.getWorld());

        if (oldWorld == null) return;

        player.teleportAsync(
                new Location(
                        oldWorld,
                        playerSave.getX(),
                        playerSave.getY(),
                        playerSave.getZ()
                )
        );
    }

    public static void setMaxTime(Player player, long maxTime) {
        DBDreamPlayer cache = DreamManager.getCacheDreamPlayer(player);

        if (cache == null) {
            DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
            if (dreamPlayer == null) return;

            DreamManager.saveDreamPlayerData(dreamPlayer);
            cache = DreamManager.getCacheDreamPlayer(player);
            if (cache == null) {
                OMCPlugin.getInstance().getSLF4JLogger().warn("player ({}) had no cache even after saving it. [DreamManager#setMaxTime]", player.getUniqueId());
                return;
            }
        }

        cache.setMaxDreamTime(maxTime);
        DreamManager.saveDreamPlayerData(cache);
    }

    public static double calculateDreamProbability(Player player) {
        double base = 0.2;
        PlayerInventory inv = player.getInventory();

        ItemStack[] armor = {
                inv.getHelmet(),
                inv.getChestplate(),
                inv.getLeggings(),
                inv.getBoots()
        };

        for (ItemStack item : armor) {
            DreamItem dream = DreamItemRegistry.getByItemStack(item);

            if (dream != null && dream.getName().contains("omc_dream:pyjama")) {
                base += 0.05;
            }
        }

        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city != null && PerkManager.hasPerk(city.getMayor(), Perks.GREAT_SLEEPER.getId())) {
            base += 0.4;
        }
        return base;
    }

    public static void tpPlayerDream(Player player) {
        Biome biome = DreamBiome.SCULK_PLAINS.getBiome();
        World dreamWorld = Bukkit.getWorld(DreamDimensionManager.DIMENSION_NAME);

        if (dreamWorld == null) return;

        Location spawningLocation = LocationUtils.findLocationInBiome(dreamWorld, biome);

        if (spawningLocation == null) return;

        player.teleportAsync(spawningLocation);
    }

    public static void tpPlayerToLastDreamLocation(Player player) {
        DBDreamPlayer dbDreamPlayer = getCacheDreamPlayer(player);
        if (dbDreamPlayer == null) return;

        player.teleportAsync(new Location(
                Bukkit.getWorld(DreamDimensionManager.DIMENSION_NAME),
                dbDreamPlayer.getDreamX(),
                dbDreamPlayer.getDreamY(),
                dbDreamPlayer.getDreamZ()
        ));
    }
}
