package fr.openmc.core.features.events.contents.halloween.halloween;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.events.contents.halloween.halloween.dimension.advancement.Advancements;
import fr.openmc.core.features.events.contents.halloween.halloween.model.HalloweenDB;
import fr.openmc.core.features.events.contents.halloween.halloween.shop.WitchShopManager;
import lombok.Getter;

import java.sql.SQLException;
import java.util.*;

public class HalloweenManager extends Feature implements LoadAfterItemsAdder, HasDatabase, HasListeners, HasCommands {

    @Getter
    private static HalloweenDB halloweenDB;

    private static Dao<HalloweenDB, String> halloweenDao;

    public static List<String> unlockedRooms;

    @Override
    public void init() {
        halloweenDB = loadEvent();
        unlockedRooms = halloweenDB.getUnlockedRooms();
        WitchShopManager.init();
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
            HalloweenDB halloweenDB = halloweenDao.queryForFirst();
            if (halloweenDB == null)
                halloweenDB = new HalloweenDB(0, List.of(Advancements.ROOM_1.name()), false);
            return halloweenDB;
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

    public static boolean hasUnlock(Advancements advancements) {
        return unlockedRooms.contains(advancements.name());
    }

    public static boolean unlockNewRoom(Advancements advancements) {
        return unlockedRooms.add(advancements.name());
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(

        );
    }
}
