package fr.openmc.core.features.economy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.database.DatabaseManager;

public class EconomyData {

    public static void init_db(Connection connection) throws SQLException {
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS economie (player VARCHAR(36) NOT NULL PRIMARY KEY , balance DOUBLE DEFAULT 0);").executeUpdate();
    }

    public static void saveBalances(UUID player, double balance) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT balance, player FROM economie WHERE player = ?");
                statement.setString(1, player.toString());
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    statement = connection.prepareStatement("UPDATE economie SET balance = ? WHERE player = ?");
                    statement.setDouble(1, balance);
                    statement.setString(2, player.toString());
                } else {
                    statement = connection.prepareStatement("INSERT INTO economie (player, balance) VALUES (?, ?)");
                    statement.setString(1, player.toString());
                    statement.setDouble(2, balance);
                }
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Map<UUID, Double> loadBalances() {
        try {
            Map<UUID, Double> balances = new HashMap<>();

            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT player, balance FROM economie");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UUID player = UUID.fromString(resultSet.getString("player"));
                double balance = resultSet.getDouble("balance");
                balances.put(player, balance);
            }

            statement.close();
            return balances;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}