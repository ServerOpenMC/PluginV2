package fr.openmc.core.utils.init;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Interface permettant aux features d'initialiser leur base de données lors du démarrage du plugin.
 */
public interface DatabaseFeature {
    void initDB(ConnectionSource connectionSource) throws SQLException;
}

