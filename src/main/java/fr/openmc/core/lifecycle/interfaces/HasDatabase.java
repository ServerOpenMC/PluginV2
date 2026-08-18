package fr.openmc.core.lifecycle.interfaces;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Interface permettant aux classes d'initialiser leur base de données lors du démarrage du plugin.
 */
public interface HasDatabase {
    /**
     * Initialise les structures et acces DB de la feature.
     *
     * @param connectionSource Source de connexion ORMLite
     * @throws SQLException Si l'initialisation DB échoue
     */
    void initDB(ConnectionSource connectionSource) throws SQLException;
}
