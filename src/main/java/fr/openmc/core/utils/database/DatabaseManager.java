package fr.openmc.core.utils.database;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.init.DatabaseFeature;
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
}
