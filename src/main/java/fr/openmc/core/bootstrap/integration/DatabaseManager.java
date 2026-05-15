package fr.openmc.core.bootstrap.integration;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.LocalLogBackend;
import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.types.DatabaseFeature;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.channels.ConnectionPendingException;
import java.sql.SQLException;

/**
 * Gere la connexion base de donnees et l'initialisation des features persistantes.
 */
public class DatabaseManager {
    @Getter
    private static ConnectionSource connectionSource;

    /**
     * Initialise le driver, la connexion pool et les features de type DB.
     *
     * @throws RuntimeException Si le driver ou la connexion DB échoue
     */
    public static void init() {
        try {
            if (OMCPlugin.isUnitTestVersion()) {
                Class.forName("org.h2.Driver");
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
        } catch (ClassNotFoundException e) {
            OMCLogger.error("Database driver not found. Please ensure the MySQL or H2 driver is included in the classpath.");
            throw new RuntimeException(e);
        }

        // ormlite
        try {
            FileConfiguration config = OMCPlugin.getConfigs();
            String databaseUrl = config.getString("database.url");
            String username = config.getString("database.username");
            String password = config.getString("database.password");
            connectionSource = new JdbcPooledConnectionSource(databaseUrl, username, password);

            OMCPlugin.getInstance().REGISTRY_FEATURE.stream()
                            .filter(f -> f instanceof DatabaseFeature)
                    .forEach(f -> {
                        try {
                            f.startDB(connectionSource);
                        } catch (SQLException e) {
                            OMCLogger.error("Failed to initialize the database connection.", e);
                            throw new RuntimeException(e);
                        } catch (ConnectionPendingException e) {
                            OMCLogger.error("Database connection is pending. Please check your database configuration.");
                            throw new RuntimeException(e);
                        }
                    });
        } catch (SQLException e) {
            OMCLogger.error("Failed to initialize the database connection.", e);
            throw new RuntimeException(e);
        } catch (ConnectionPendingException e) {
            OMCLogger.error("Database connection is pending. Please check your database configuration.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Filtre les logs OrmLite trop verbeux lors du demarrage.
     */
    public static class ShutUpOrmLite extends LocalLogBackend {
        private final String classLabel;

        /**
         * Crée un filtre de logs OrmLite pour une classe donnée.
         *
         * @param classLabel Label de classe ORMLite
         */
        public ShutUpOrmLite(String classLabel) {
            super(classLabel);
            this.classLabel = classLabel;
        }

        /**
         * Indique si un niveau est autorisé.
         *
         * @param level Niveau ORMLite
         * @return True si le niveau est autorisé
         */
        @Override
        public boolean isLevelEnabled(Level level) {
            return Level.INFO.isEnabled(level);
        }

        /**
         * Log un message ORMLite si non filtré.
         *
         * @param level Niveau du log
         * @param msg Message
         */
        @Override
        public void log(Level level, String msg) {
            if (classLabel.contains("com.j256.ormlite.table.TableUtils") || msg.contains("DaoManager created dao for class class"))
                return;

            super.log(level, msg);
        }

        /**
         * Log un message ORMLite avec exception si non filtré.
         *
         * @param level Niveau du log
         * @param msg Message
         * @param throwable Exception associée
         */
        @Override
        public void log(Level level, String msg, Throwable throwable) {
            if (classLabel.contains("com.j256.ormlite.table.TableUtils") || msg.contains("DaoManager created dao for class class"))
                return;

            super.log(level, msg, throwable);
        }
    }
}
