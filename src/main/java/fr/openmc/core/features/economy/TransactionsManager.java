package fr.openmc.core.features.economy;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.analytics.Stats;
import fr.openmc.core.utils.database.DatabaseManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class TransactionsManager {
    private static Dao<Transaction, String> transactionsDao;

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Transaction.class);
        transactionsDao = DaoManager.createDao(connectionSource, Transaction.class);
    }

    public static List<Transaction> getTransactionsByPlayers(UUID player, int limit) {
        if (!OMCPlugin.getConfigs().getBoolean("features.transactions", false)) {
            return List.of(new Transaction("CONSOLE", "CONSOLE", 0, "Désactivé"));
        }

        try {
            QueryBuilder<Transaction, String> query = transactionsDao.queryBuilder();
            query.where().eq("recipient", player.toString()).or().eq("sender", player.toString());
            return transactionsDao.query(query.prepare());
        } catch (SQLException err) {
            err.printStackTrace();
            return List.of(new Transaction("CONSOLE", "CONSOLE", 0, "ERREUR"));
        }
    }

    public static boolean registerTransaction(Transaction transaction) {
        if (!OMCPlugin.getConfigs().getBoolean("features.transactions", false)) {
            return true;
        }

        if (!Objects.equals(transaction.sender, "CONSOLE")) {
            Stats.TOTAL_TRANSACTIONS.increment(UUID.fromString(transaction.sender));
        }

        if (!Objects.equals(transaction.recipient, "CONSOLE")) {
            Stats.TOTAL_TRANSACTIONS.increment(UUID.fromString(transaction.recipient));
        }

        try {
            return transactionsDao.create(transaction) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
