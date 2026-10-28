package fr.openmc.core.registry.structures.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.LoadIfEnable;
import fr.openmc.core.hooks.WorldEditHook;
import fr.openmc.core.utils.FilesUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Provides simple synchronous database access for placed structures.
 */
public class CustomStructureManager extends Feature implements
        LoadIfEnable<WorldEditHook>, HasDatabase {

    private static Dao<DBPlacedStructure, UUID> dao;

    public static final File STRUCTURE_FOLDER =
            new File(OMCPlugin.getInstance().getDataFolder(), "data/omc_structure");

    @Override
    public void init() {
        try {
            FilesUtils.copyResourceFolder("data/omc_structure", STRUCTURE_FOLDER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBPlacedStructure.class);
        dao = DaoManager.createDao(connectionSource, DBPlacedStructure.class);
    }

    public static void savePlacement(DBPlacedStructure placement) throws SQLException {
        requireInitialized();
        dao.create(placement);
    }

    public static DBPlacedStructure getPlacement(UUID id) throws SQLException {
        requireInitialized();
        return dao.queryForId(id);
    }

    public static boolean existsPlacement(UUID id) throws SQLException {
        requireInitialized();
        return dao.idExists(id);
    }

    public static List<DBPlacedStructure> getPlacements(String structureId) throws SQLException {
        requireInitialized();
        return dao.queryBuilder()
                .orderBy("createdAt", false)
                .where()
                .eq("structureId", structureId)
                .query();
    }

    public static void deletePlacement(UUID id) throws SQLException {
        requireInitialized();
        dao.deleteById(id);
    }

    public static boolean actionAllow() {
        return WorldEditHook.isEnable();
    }

    private static void requireInitialized() {
        if (dao == null) {
            throw new IllegalStateException("CustomStructureManager database has not been initialized.");
        }
    }
}
