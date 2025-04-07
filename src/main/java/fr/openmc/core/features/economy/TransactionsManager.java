package fr.openmc.core.features.economy;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionsManager {
    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS transactions (recipient VARCHAR(36), sender VARCHAR(36), amount DOUBLE, reason VARCHAR(255), date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)").executeUpdate();
    }

    public static List<Transaction> getTransactionsByPlayers(UUID player, int limit) {
        if (!OMCPlugin.getConfigs().getBoolean("features.transactions", false)) {
            return List.of(new Transaction("CONSOLE", "CONSOLE", 0, "Désactivé"));
        }

        List<Transaction> transactions = new ArrayList<>();
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT recipient, sender, amount, reason FROM transactions WHERE recipient = ? OR sender = ? ORDER BY date DESC LIMIT ?");
            statement.setString(1, player.toString());
            statement.setString(2, player.toString());
            statement.setInt(3, limit);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getString("recipient"),
                        rs.getString("sender"),
                        rs.getDouble("amount"),
                        rs.getString("reason")
                ));
            }

            return transactions;
        } catch (SQLException err) {
            err.printStackTrace();
            return List.of(new Transaction("CONSOLE", "CONSOLE", 0, "ERREUR"));
        }
    }
}