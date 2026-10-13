package fr.openmc.core.features.economy;

import com.j256.ormlite.stmt.QueryBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.analytics.Stats;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.db.DBTable;
import fr.openmc.core.lifecycle.interfaces.db.HasDatabase;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Credit(developers = {"Axeno", "Piquel Chips", "PuppyTransGirl", "Gyro"})
public class TransactionsManager extends Feature implements HasDatabase {
    private final DBTable<Transaction, String> transactionsTable = DBTable.of(Transaction.class);

    @Override
    public List<DBTable<?, ?>> getTables() {
        return List.of(transactionsTable);
    }

    public List<Transaction> getTransactionsByPlayers(UUID playerUUID) {
        if (!OMCPlugin.getConfigs().getBoolean("features.transactions", false)) {
            return List.of(new Transaction("CONSOLE", "CONSOLE", 0, "Désactivé"));
        }

        try {
            QueryBuilder<Transaction, String> query = transactionsTable.queryBuilder();
            query.where().eq("recipient", playerUUID.toString()).or().eq("sender", playerUUID.toString());
            return transactionsTable.query(query.prepare());
        } catch (SQLException err) {
            OMCLogger.error("Failed to get transactions {}", playerUUID, err);
            return List.of(new Transaction("CONSOLE", "CONSOLE", 0, "ERREUR"));
        }
    }

    public void registerTransaction(Transaction transaction) {
        if (!OMCPlugin.getConfigs().getBoolean("features.transactions", false)) return;

        if (!Objects.equals(transaction.sender, "CONSOLE"))
            Stats.TOTAL_TRANSACTIONS.increment(UUID.fromString(transaction.sender));

        if (!Objects.equals(transaction.recipient, "CONSOLE"))
            Stats.TOTAL_TRANSACTIONS.increment(UUID.fromString(transaction.recipient));


        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                transactionsTable.create(transaction);
            } catch (SQLException e) {
                OMCLogger.error("Failed to register transactions", e);
            }
        });
    }
}
