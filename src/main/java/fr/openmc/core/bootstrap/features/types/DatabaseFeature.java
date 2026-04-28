package fr.openmc.core.bootstrap.features.types;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Interface permettant aux features d'initialiser leur base de données lors du démarrage du plugin.
 */
public interface DatabaseFeature {
    /**
     * Initialise les structures et acces DB de la feature.
     *
     * @param connectionSource Source de connexion ORMLite
     * @throws SQLException Si l'initialisation DB échoue
     */
    void initDB(ConnectionSource connectionSource) throws SQLException;
}
