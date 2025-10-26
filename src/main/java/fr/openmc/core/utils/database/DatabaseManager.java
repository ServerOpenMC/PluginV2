package fr.openmc.core.utils.database;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.analytics.AnalyticsManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.rank.CityRankManager;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.TransactionsManager;
import fr.openmc.core.features.events.halloween.HalloweenManager;
import fr.openmc.core.features.friend.FriendSQLManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.settings.PlayerSettingsManager;
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

            WarManager.initDB(connectionSource);
            NotationManager.initDB(connectionSource);
            MayorManager.initDB(connectionSource);
            MilestonesManager.initDB(connectionSource);
            BankManager.initDB(connectionSource);
            TransactionsManager.initDB(connectionSource);
            AnalyticsManager.initDB(connectionSource);
            MailboxManager.initDB(connectionSource);
            ContestManager.initDB(connectionSource);
            EconomyManager.initDB(connectionSource);
            HomesManager.initDB(connectionSource);
            FriendSQLManager.initDB(connectionSource);
            DynamicCooldownManager.initDB(connectionSource);
            CityManager.initDB(connectionSource);
            CityRankManager.initDB(connectionSource);
            MascotsManager.initDB(connectionSource);
            PlayerSettingsManager.initDB(connectionSource);
            CityStatisticsManager.initDB(connectionSource);
            HalloweenManager.initDB(connectionSource);
        } catch (SQLException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Failed to initialize the database connection.", e);
            throw new RuntimeException(e);
        } catch (ConnectionPendingException e) {
            OMCPlugin.getInstance().getSLF4JLogger().error("Database connection is pending. Please check your database configuration.");
            throw new RuntimeException(e);
        }
    }
}
