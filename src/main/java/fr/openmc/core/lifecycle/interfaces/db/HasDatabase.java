package fr.openmc.core.lifecycle.interfaces.db;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface permettant aux classes d'initialiser leur base de données lors du démarrage du plugin.
 */
public interface HasDatabase {
    List<DBTable<?, ?>> getTables();

    // * Override si envie
    default void onInitDB(ConnectionSource connectionSource) throws SQLException {};

    /**
     * Initialise les structures et acces DB de la feature.
     *
     * @param connectionSource Source de connexion ORMLite
     * @throws SQLException Si l'initialisation DB échoue
     */
    default void initDB(ConnectionSource connectionSource) throws SQLException {
        onInitDB(connectionSource);

        for (DBTable<?, ?> table : getTables())
            table.init(connectionSource);
    }

}
