package fr.openmc.core.features.dream;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.commands.AdminDreamCommands;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.generation.listeners.CloudStructureDispenserListener;
import fr.openmc.core.features.dream.generation.listeners.ReplaceBlockListener;
import fr.openmc.core.features.dream.generation.structures.DreamStructuresManager;
import fr.openmc.core.features.dream.listeners.biomes.PlayerEnteredBiome;
import fr.openmc.core.features.dream.listeners.dream.*;
import fr.openmc.core.features.dream.listeners.orb.PlayerObtainOrb;
import fr.openmc.core.features.dream.listeners.others.CraftingConvertorListener;
import fr.openmc.core.features.dream.listeners.others.PlayerEatSomnifere;
import fr.openmc.core.features.dream.mecanism.cloudcastle.BossCloudSpawner;
import fr.openmc.core.features.dream.mecanism.cloudcastle.CloudVault;
import fr.openmc.core.features.dream.mecanism.cloudfishing.CloudFishingManager;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.models.db.OldInventory;
import fr.openmc.core.features.dream.registries.*;
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
    // ** CONSTANTS **
    public static final Long BASE_DREAM_TIME = 300L;

    private static final HashMap<UUID, DreamPlayer> dreamPlayerData = new HashMap<>();
    public static final HashMap<UUID, DBDreamPlayer> cacheDreamPlayer = new HashMap<>();

    private static Dao<DBDreamPlayer, String> dreamPlayerDao;

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
                new ReplaceBlockListener(),
                new PlayerEatSomnifere(),
                new CloudVault(),
                new BossCloudSpawner(),
                new CloudStructureDispenserListener(),
                new CraftingConvertorListener()
        );

        // ** MANAGERS **
        DreamEnchantementRegistry.init();
        DreamDimensionManager.init();
        DreamStructuresManager.init();
        DreamItemRegistry.init();
        DreamBlocksRegistry.init();
        DreamMobsRegistry.init();
        DreamBlocksDropsRegistry.init();
        CloudFishingManager.init();

        // ** COMMANDS **
        CommandsManager.getHandler().register(
                new AdminDreamCommands()
        );

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
            if (!(entity instanceof Player player)) {
                entity.remove();
            } else {
                removeDreamPlayer(player, player.getLocation());
            }
        }
    }

    private static void loadAllDreamPlayerData() {
        try {
            dreamPlayerData.clear();
            dreamPlayerDao.queryForAll().forEach(playerData ->
                    cacheDreamPlayer.put(playerData.getPlayerUUID(), playerData)
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveAllDreamPlayerData() {
        dreamPlayerData.forEach((uuid, dreamPlayer) ->
                saveDreamPlayerData(dreamPlayer)
        );
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

    public static void addDreamPlayer(Player player, Location oldLocation) throws IOException {
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

        dreamPlayerData.put(player.getUniqueId(), new DreamPlayer(player, oldInv, oldLocation, dreamPlayerInv));
    }

    public static void removeDreamPlayer(Player player, Location dreamLocation) {
        DreamPlayer dreamPlayer = dreamPlayerData.remove(player.getUniqueId());

        if (dreamPlayer == null) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("Cannot remove player {}({}) from Dream", player.getName(), player.getUniqueId());
            return;
        }

        dreamPlayer.cancelTask();

        OldInventory oldInventory = dreamPlayer.getOldInventory();
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

        oldInventory.restoreOldInventory(player);
    }

    public static double calculateDreamProbability(Player player) {
        double base = 0.4;
        // si le joueur porte un pijama proba augmenté / armure ornique
        // maire ect...

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
