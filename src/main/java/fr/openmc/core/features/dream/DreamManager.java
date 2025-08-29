package fr.openmc.core.features.dream;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.listeners.PlayerChangeWorldListener;
import fr.openmc.core.features.dream.listeners.PlayerQuitListener;
import fr.openmc.core.features.dream.models.DBDreamPlayer;
import fr.openmc.core.features.dream.models.DreamPlayer;
import fr.openmc.core.features.dream.models.OldInventory;
import fr.openmc.core.features.dream.spawning.DreamSpawningManager;
import fr.openmc.core.utils.serializer.BukkitSerializer;
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

    public DreamManager() {
        OMCPlugin.registerEvents(
                new PlayerChangeWorldListener(),
                new PlayerQuitListener()
        );

        // ** MANAGERS **
        new DreamDimensionManager();
        new DreamSpawningManager();

        // ** LOAD DATAS **
        loadAllDreamPlayerData();
    }

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBDreamPlayer.class);
        dreamPlayerDao = DaoManager.createDao(connectionSource, DBDreamPlayer.class);
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
        try {
            dreamPlayerDao.createOrUpdate(dreamPlayer.serialize());
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

        dreamPlayerData.put(player.getUniqueId(), new DreamPlayer(player, oldInv, dreamPlayerInv));
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
}
