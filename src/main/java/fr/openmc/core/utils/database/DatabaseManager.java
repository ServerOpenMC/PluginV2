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
import lombok.Getter;

import org.bukkit.configuration.file.FileConfiguration;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class DatabaseManager {
    @Getter
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
            DynamicCooldownManager.init_db(connectionSource);
            CompanyManager.init_db(connectionSource);
            CityManager.init_db(connectionSource);
            MascotsManager.init_db(connectionSource);
            MayorManager.init_db(connectionSource);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            OMCPlugin.getInstance().getLogger().severe("Impossible d'initialiser la base de données");
        }
    }
}
