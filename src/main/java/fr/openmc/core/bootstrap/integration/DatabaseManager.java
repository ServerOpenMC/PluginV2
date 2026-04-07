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

public class DatabaseManager {
    @Getter
    private static ConnectionSource connectionSource;

    public static void init() {
        try {
            if (OMCPlugin.isUnitTestVersion()) {
                Class.forName("org.h2.Driver");
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
        } catch (ClassNotFoundException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Database driver not found. Please ensure the MySQL or H2 driver is included in the classpath.");
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
                            ((DatabaseFeature) f).initDB(connectionSource);
                        } catch (SQLException e) {
                            OMCPlugin.getInstance().getSLF4JLogger().error("Failed to initialize the database connection.", e);
                            throw new RuntimeException(e);
                        } catch (ConnectionPendingException e) {
                            OMCPlugin.getInstance().getSLF4JLogger().error("Database connection is pending. Please check your database configuration.");
                            throw new RuntimeException(e);
                        }
                    });
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Failed to initialize the database connection.", e);
            throw new RuntimeException(e);
        } catch (ConnectionPendingException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Database connection is pending. Please check your database configuration.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Désactive les loggers venant de OrmLite (création de table ect...)
     */
    public static class ShutUpOrmLite extends LocalLogBackend {
        private final String classLabel;

        public ShutUpOrmLite(String classLabel) {
            super(classLabel);
            this.classLabel = classLabel;
        }

        @Override
        public boolean isLevelEnabled(Level level) {
            return Level.INFO.isEnabled(level);
        }

        @Override
        public void log(Level level, String msg) {
            if (classLabel.contains("com.j256.ormlite.table.TableUtils") || msg.contains("DaoManager created dao for class class"))
                return;

            super.log(level, msg);
        }

        @Override
        public void log(Level level, String msg, Throwable throwable) {
            if (classLabel.contains("com.j256.ormlite.table.TableUtils") || msg.contains("DaoManager created dao for class class"))
                return;

            super.log(level, msg, throwable);
        }
    }
}
