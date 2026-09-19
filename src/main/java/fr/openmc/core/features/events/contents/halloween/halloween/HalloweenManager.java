package fr.openmc.core.features.events.contents.halloween.halloween;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
import fr.openmc.core.features.events.contents.halloween.halloween.model.HalloweenDB;
import lombok.Getter;

import java.sql.SQLException;
import java.util.*;

public class HalloweenManager extends Feature implements HasDatabase, HasListeners, HasCommands {

    @Getter
    private static HalloweenDB halloweenDB;

    private static Dao<HalloweenDB, String> halloweenDao;

    public static String[] unlockedRooms;

    @Override
    public void init() {
        CorpseNPCManager.init();
        halloweenDB = loadEvent();
        unlockedRooms = halloweenDB.getContent();
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, HalloweenDB.class);
        halloweenDao = DaoManager.createDao(connectionSource, HalloweenDB.class);
    }

    @Override
    protected void save() {
        saveEvent();
    }

    public static HalloweenDB loadEvent() {
        try {
            return halloweenDao.queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveEvent() {
        try {
            halloweenDao.createOrUpdate(halloweenDB);
        } catch (Exception e) {
            OMCLogger.error("Impossible de sauvegarder les corpses pendant l'arret.", e);
        }
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of();
    }
}
