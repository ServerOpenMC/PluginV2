package fr.openmc.core.features.events.halloween;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.halloween.models.HalloweenData;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class HalloweenManager {
    private static Object2ObjectMap<UUID, HalloweenData> halloweenData;
    private static Dao<HalloweenData, String> halloweenDataDao;

    public static void init() {
        halloweenData = loadAllHalloweenDatas();
    }

    public static void depositPumpkins(UUID playerUUID, int amount) {
        HalloweenData data = halloweenData.get(playerUUID);
        data.depositPumpkins(amount);
        saveHalloweenData(data);
    }

    public static int getPumpkinCount(UUID playerUUID) {
        HalloweenData data = halloweenData.computeIfAbsent(playerUUID, HalloweenData::new);
        return data.getPumpkinCount();
    }

    public static Object2ObjectMap<UUID, HalloweenData> getAllHalloweenData() {
        return halloweenData;
    }

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, HalloweenData.class);
        halloweenDataDao = DaoManager.createDao(connectionSource, HalloweenData.class);
    }

    private static boolean saveHalloweenData(HalloweenData data) {
        try {
            halloweenDataDao.createOrUpdate(data);
            return true;
        } catch (SQLException e) {
            OMCPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to save halloween data " + data.getPlayerUUID(), e);
            return false;
        }
    }

    private static Object2ObjectMap<UUID, HalloweenData> loadAllHalloweenDatas() {
        Object2ObjectMap<UUID, HalloweenData> newHalloweenDatas = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
        try {
            List<HalloweenData> halloweenDataDBs = halloweenDataDao.queryForAll();
            for (HalloweenData halloweenData : halloweenDataDBs) {
                newHalloweenDatas.put(halloweenData.getPlayerUUID(), halloweenData);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newHalloweenDatas;
    }
}
