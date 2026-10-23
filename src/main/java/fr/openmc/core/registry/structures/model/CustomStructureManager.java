package fr.openmc.core.registry.structures.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.hooks.WorldEditHook;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Feature qui porte uniquement la table des undos de structures.
 * Les accès DB sont synchrones : à appeler hors du thread principal (voir CustomStructure).
 */
public class CustomStructureManager extends Feature implements HasDatabase {

    private static Dao<DBPlacedStructure, UUID> dao;

    public static boolean WELoaded = false;

    @Override
    public void init() {
        WELoaded = WorldEditHook.isEnable();
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBPlacedStructure.class);
        dao = DaoManager.createDao(connectionSource, DBPlacedStructure.class);
    }

//    @Override
//    protected void save() {
//
//    }

    public static void savePlacement(DBPlacedStructure p) throws SQLException {
        dao.create(p);
    }

    public static DBPlacedStructure getPlacement(UUID id) throws SQLException {
        return dao.queryForId(id);
    }

    public static boolean existsPlacement(UUID id) throws SQLException {
        return dao.idExists(id);
    }

    public static DBPlacedStructure getLatestPlacement(String structureId) throws SQLException {
        return dao.queryBuilder()
                .orderBy("createdAt", false)
                .where().eq("structureId", structureId)
                .queryForFirst();
    }

    /** Du plus récent au plus ancien. */
    public static List<DBPlacedStructure> getPlacements(String structureId) throws SQLException {
        return dao.queryBuilder()
                .orderBy("createdAt", false)
                .where().eq("structureId", structureId)
                .query();
    }

    public static void deletePlacement(UUID id) throws SQLException {
        dao.deleteById(id);
    }

    public static boolean actionAllow() {
        if (!WELoaded)
            OMCLogger.warn("Action prevent from use because WE or FAWE not enable");
        return WELoaded;
    }
}

