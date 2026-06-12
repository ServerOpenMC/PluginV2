package fr.openmc.core.features.dungeons;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.DatabaseFeature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.dungeons.db.DBKeyVault;
import lombok.Getter;

import java.sql.SQLException;
import java.util.*;

public class DungeonsManager extends Feature implements DatabaseFeature, HasCommands {

    @Getter
    public static Map<UUID, List<DBKeyVault>> dataKeyVault = new HashMap<>();
    private static Dao<DBKeyVault, String> keyVaultDao;

    @Override
    public void init() {



    }



    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {

        TableUtils.createTableIfNotExists(connectionSource, DBKeyVault.class);
        keyVaultDao = DaoManager.createDao(connectionSource, DBKeyVault.class);

    }

    public static void loadKeyVaultPlayerData() {
        try {
            keyVaultDao.queryForAll().forEach(keyVault -> dataKeyVault.computeIfAbsent(keyVault.getPlayerUUID(), l->
                new ArrayList<>()
            ).add(keyVault));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sauvegarde les données des joueurs (points, camp, etc.) dans la DB.
     */
    public static void saveKeyVaultPlayerData() {
        OMCLogger.info("Saving Key vault data...");
        dataKeyVault.forEach((player, data) -> {
            data.forEach(keyVault -> {
                try {
                    keyVaultDao.createOrUpdate(keyVault);
                } catch (SQLException e) {
                    OMCLogger.error("Cannot register key vault : ", e);
                }
            });
        });
        OMCLogger.info("Key vault data saved successfully.");
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(new DungeonsCommand());
    }
}
