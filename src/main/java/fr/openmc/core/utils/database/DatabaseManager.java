package fr.openmc.core.utils.database;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.analytics.AnalyticsManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.economy.TransactionsManager;
import fr.openmc.core.features.friend.FriendSQLManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static Connection connection;
    private static ConnectionSource connectionSource;

    public DatabaseManager() {
        // ormlite
        try {
            if (OMCPlugin.isUnitTestVersion()) {
                Class.forName("org.h2.Driver");
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }

            FileConfiguration config = OMCPlugin.getConfigs();
            String databaseUrl = config.getString("database.url");
            String username = config.getString("database.username");
            String password = config.getString("database.password");
            connectionSource = new JdbcConnectionSource(databaseUrl, username, password);

            BankManager.init_db(connectionSource);
            TransactionsManager.init_db(connectionSource);
            AnalyticsManager.init_db(connectionSource);
            ContestManager.init_db(connectionSource);
            MailboxManager.init_db(connectionSource);
            EconomyManager.init_db(connectionSource);
            HomesManager.init_db(connectionSource);
            FriendSQLManager.init_db(connectionSource);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            OMCPlugin.getInstance().getLogger().severe("Impossible d'initialiser la base de données");
        }

        // old database connection setup
        connect();
        try {
            // Déclencher au début du plugin pour créer les tables nécessaires
            CityManager.init_db(connection);
            MayorManager.init_db(connection);
            MascotsManager.init_db(connection);
            DynamicCooldownManager.init_db(connection);
            CompanyManager.init_db(connection);

        } catch (SQLException e) {
            e.printStackTrace();
            OMCPlugin.getInstance().getLogger().severe("Impossible d'initialiser la base de données");
        }
    }

    private static void connect() {
        try {
            if (OMCPlugin.isUnitTestVersion()) {
                Class.forName("org.h2.Driver");
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }

            FileConfiguration config = OMCPlugin.getConfigs();

            if (!(config.contains("database.url") || config.contains("database.username")
                    || config.contains("database.password"))) {
                OMCPlugin.getInstance().getLogger().severe("Impossible de se connecter à la base de données");
                Bukkit.getPluginManager().disablePlugin(OMCPlugin.getInstance());
            }

            connection = DriverManager.getConnection(
                    config.getString("database.url"),
                    config.getString("database.username"),
                    config.getString("database.password"));
            OMCPlugin.getInstance().getLogger().info("\u001B[32m" + "Connexion à la base de données réussie\u001B[0m");
        } catch (SQLException | ClassNotFoundException e) {
            OMCPlugin.getInstance().getLogger()
                    .warning("\u001B[31m" + "Connexion à la base de données échouée\u001B[0m");
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

    public static ConnectionSource getConnectionSource() {
        return connectionSource;
    }
}
