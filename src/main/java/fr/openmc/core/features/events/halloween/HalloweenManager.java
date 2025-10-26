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
import java.util.UUID;
import java.util.logging.Level;

public class HalloweenManager {
    private static Object2ObjectMap<UUID, HalloweenData> halloweenData;
    private static Dao<HalloweenData, String> halloweenDataDao;

    public static void init() {
        halloweenData = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    }

    public static void depositPumpkins(UUID playerUUID, int amount) {
        HalloweenData data = halloweenData.get(playerUUID);
        data.depositPumpkins(amount);
    }

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, HalloweenData.class);
        halloweenDataDao = DaoManager.createDao(connectionSource, HalloweenData.class);
    }

    private static boolean saveHalloweenData(HalloweenData data) {
        try {
            halloweenData.put(data.getPlayerUUID(), data);
            halloweenDataDao.createOrUpdate(data);
            return true;
        } catch (SQLException e) {
            OMCPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to save halloween data " + data.getPlayerUUID(), e);
            return false;
        }
    }
}
