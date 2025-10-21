package fr.openmc.core.features.economy;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.analytics.Stats;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class TransactionsManager {
    private static Dao<Transaction, String> transactionsDao;

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Transaction.class);
        transactionsDao = DaoManager.createDao(connectionSource, Transaction.class);
    }

    public static List<Transaction> getTransactionsByPlayers(UUID playerUUID, int limit) {
        if (!OMCPlugin.getConfigs().getBoolean("features.transactions", false)) {
            return List.of(new Transaction("CONSOLE", "CONSOLE", 0, "Désactivé"));
        }

        try {
            QueryBuilder<Transaction, String> query = transactionsDao.queryBuilder();
            query.where().eq("recipient", playerUUID.toString()).or().eq("sender", playerUUID.toString());
            return transactionsDao.query(query.prepare());
        } catch (SQLException err) {
            OMCPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to get transactions " + playerUUID, err);
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
            OMCPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to register transactions", e);
            return false;
        }
    }
}
