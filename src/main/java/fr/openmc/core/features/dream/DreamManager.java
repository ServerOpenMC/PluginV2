package fr.openmc.core.features.dream;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.blocks.DreamBlocksManager;
import fr.openmc.core.features.dream.blocks.cloudspawner.MobCloudSpawner;
import fr.openmc.core.features.dream.blocks.cloudvault.CloudVault;
import fr.openmc.core.features.dream.crafting.DreamCraftingRegister;
import fr.openmc.core.features.dream.drops.DreamDropsManager;
import fr.openmc.core.features.dream.fishing.CloudFishingManager;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.generation.structures.DreamStructuresManager;
import fr.openmc.core.features.dream.items.DreamItemRegister;
import fr.openmc.core.features.dream.listeners.biomes.PlayerEnteredBiome;
import fr.openmc.core.features.dream.listeners.dream.PlayerChangeWorldListener;
import fr.openmc.core.features.dream.listeners.dream.PlayerQuitListener;
import fr.openmc.core.features.dream.listeners.dream.PlayerSleepListener;
import fr.openmc.core.features.dream.listeners.generation.ReplaceBlockListener;
import fr.openmc.core.features.dream.listeners.orb.PlayerObtainOrb;
import fr.openmc.core.features.dream.listeners.others.PlayerEatSomnifere;
import fr.openmc.core.features.dream.mobs.DreamMobManager;
import fr.openmc.core.features.dream.models.DBDreamPlayer;
import fr.openmc.core.features.dream.models.DreamPlayer;
import fr.openmc.core.features.dream.models.OldInventory;
import fr.openmc.core.utils.LocationUtils;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class DreamManager {
    //TODO: REMPLACE ALL PLUGIN.LOGGER BY getSLF4JLogger

    // ** CONSTANTS **
    public static final Long BASE_DREAM_TIME = 300L;

    private static final HashMap<UUID, DreamPlayer> dreamPlayerData = new HashMap<>();
    public static final HashMap<UUID, DBDreamPlayer> cacheDreamPlayer = new HashMap<>();

    private static Dao<DBDreamPlayer, String> dreamPlayerDao;

    public DreamManager() {
        OMCPlugin.registerEvents(
                new PlayerChangeWorldListener(),
                new PlayerQuitListener(),
                //new PlayerDreamTimeEndListener(),
                new PlayerSleepListener(),
                new PlayerEnteredBiome(),
                new PlayerObtainOrb(),
                new ReplaceBlockListener(),
                new PlayerEatSomnifere(),
                new CloudVault(),
                new MobCloudSpawner()
        );

        // ** MANAGERS **
        DreamDimensionManager.init();
        DreamStructuresManager.init();
        DreamItemRegister.init();
        DreamBlocksManager.init();
        DreamMobManager.init();
        DreamDropsManager.init();
        DreamCraftingRegister.init();
        CloudFishingManager.init();

        // ** LOAD DATAS **
        loadAllDreamPlayerData();
    }

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBDreamPlayer.class);
        dreamPlayerDao = DaoManager.createDao(connectionSource, DBDreamPlayer.class);
    }

    public static void disable() {
        DreamManager.saveAllDreamPlayerData();

        World dreamWorld = Bukkit.getWorld(DreamDimensionManager.DIMENSION_NAME);

        if (dreamWorld == null) return;

        for (Entity entity : dreamWorld.getEntities()) {
            if (!(entity instanceof org.bukkit.entity.Player)) {
                entity.remove();
            }
        }
    }

    private static void loadAllDreamPlayerData() {
        try {
            dreamPlayerData.clear();
            dreamPlayerDao.queryForAll().forEach(playerData -> {
                cacheDreamPlayer.put(playerData.getPlayerUUID(), playerData);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveAllDreamPlayerData() {
        dreamPlayerData.forEach((uuid, dreamPlayer) -> {
            saveDreamPlayerData(dreamPlayer);
        });
    }

    public static void saveDreamPlayerData(DreamPlayer dreamPlayer) {
        saveDreamPlayerData(dreamPlayer.serialize());
    }

    public static void saveDreamPlayerData(DBDreamPlayer dbDreamPlayer) {
        try {
            dreamPlayerDao.createOrUpdate(dbDreamPlayer);
        } catch (SQLException e) {
            e.printStackTrace();
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

    public static void addDreamPlayer(Player player) throws IOException {
        PlayerInventory playerInv = player.getInventory();

        ItemStack[] oldContents = playerInv.getContents().clone();
        ItemStack[] oldArmor = playerInv.getArmorContents().clone();
        ItemStack[] oldExtra = playerInv.getExtraContents().clone();

        OldInventory oldInv = new OldInventory(oldContents, oldArmor, oldExtra);

        DBDreamPlayer cacheDreamPlayer = getCacheDreamPlayer(player);
        if (cacheDreamPlayer == null || cacheDreamPlayer.getDreamInventory() == null) {
            player.getInventory().clear();
        } else {
            BukkitSerializer.playerInventoryFromBase64(playerInv, cacheDreamPlayer.getDreamInventory());
            player.updateInventory();
        }

        PlayerInventory dreamPlayerInv = player.getInventory();

        dreamPlayerData.put(player.getUniqueId(), new DreamPlayer(player, oldInv, player.getLocation(), dreamPlayerInv));
    }

    public static void removeDreamPlayer(Player player) {
        DreamPlayer dreamPlayer = dreamPlayerData.remove(player.getUniqueId());
        dreamPlayer.cancelTask();

        OldInventory oldInventory = dreamPlayer.getOldInventory();
        PlayerInventory dreamInventory = player.getInventory();

        DBDreamPlayer cacheDreamPlayer = getCacheDreamPlayer(player);
        String serializedDreamInventory = BukkitSerializer.playerInventoryToBase64(dreamInventory);
        if (cacheDreamPlayer != null) {
            cacheDreamPlayer.setDreamInventory(serializedDreamInventory);
        } else {
            addCacheDreamPlayer(player, new DBDreamPlayer(player.getUniqueId(), dreamPlayer.getMaxDreamTime(), serializedDreamInventory));
        }

        oldInventory.restoreOldInventory(player);
    }

    public static double calculateDreamProbability(Player player) {
        double base = 0.4;
        // si le joueur porte un pijama proba augmenté / armure ornique
        // maire ect...

        return base;
    }

    public static void tpPlayerDream(Player player) {
        Biome biome = Biome.PLAINS;
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
