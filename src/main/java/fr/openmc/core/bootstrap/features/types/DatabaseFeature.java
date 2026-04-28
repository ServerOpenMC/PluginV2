package fr.openmc.core.bootstrap.features.types;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Interface permettant aux features d'initialiser leur base de données lors du démarrage du plugin.
 */
public interface DatabaseFeature {
    /**
     * Initialise les structures et acces DB de la feature.
     */
    void initDB(ConnectionSource connectionSource) throws SQLException;
}
