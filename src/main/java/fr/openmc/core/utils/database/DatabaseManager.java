package fr.openmc.core.utils.database;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.analytics.AnalyticsManager;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.economy.EconomyData;
import fr.openmc.core.features.economy.TransactionsManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static Connection connection;

    public DatabaseManager() {
        connect();
        try {
            // Déclencher au début du plugin pour créer les tables nécessaires
            TransactionsManager.init_db(connection);
            AnalyticsManager.init_db(connection);
            CityManager.init_db(connection);
            ContestManager.init_db(connection);
            MailboxManager.init_db(connection);
            EconomyData.init_db(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            OMCPlugin.getInstance().getLogger().severe("Impossible d'initialiser la base de données");
        }
    }

    private static void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            FileConfiguration config = OMCPlugin.getConfigs();

            if (!(config.contains("database.url") || config.contains("database.username") || config.contains("database.password"))) {
                OMCPlugin.getInstance().getLogger().severe("Impossible de se connecter à la base de données");
                Bukkit.getPluginManager().disablePlugin(OMCPlugin.getInstance());
            }

            connection = DriverManager.getConnection(
                    config.getString("database.url"),
                    config.getString("database.username"),
                    config.getString("database.password")
            );
            OMCPlugin.getInstance().getLogger().info("\u001B[32m" + "Connexion à la base de données réussie\u001B[0m");
        } catch (SQLException | ClassNotFoundException e) {
            OMCPlugin.getInstance().getLogger().warning("\u001B[31m" + "Connexion à la base de données échouée\u001B[0m");
            throw new RuntimeException(e);
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            if (!connection.isClosed()) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static Connection getConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    return connection;
                }
            } catch (SQLException e) {
                connect();
                return connection;
            }
        }
        connect();
        return connection;
    }
}
