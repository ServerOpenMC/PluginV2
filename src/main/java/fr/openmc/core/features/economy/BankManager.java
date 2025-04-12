package fr.openmc.core.features.economy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.economy.commands.BankCommands;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;

public class BankManager {
    @Getter private static Map<UUID, Double> banks;
    @Getter static BankManager instance;

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS banks (player VARCHAR(36) NOT NULL PRIMARY KEY , balance DOUBLE DEFAULT 0);").executeUpdate();
    }

    public BankManager() {
        instance = this;
        banks = loadAllBanks();

        CommandsManager.getHandler().register(new BankCommands());
    }

    public double getBankBalance(UUID player) {
        if (!banks.containsKey(player)) {
            loadPlayerBank(player);
        }

        return banks.get(player);
    }

    public void addBankBalance(UUID player, double amount) {
        if (!banks.containsKey(player)) {
            loadPlayerBank(player);
        }

        banks.put(player, banks.get(player) + amount);
        savePlayerBank(player);
    }

    public void withdrawBankBalance(UUID player, double amount) {
        if (!banks.containsKey(player)) {
            loadPlayerBank(player);
        }
        
        assert banks.get(player) > amount;

        banks.put(player, banks.get(player) - amount);
        savePlayerBank(player);
    }

    public void addBankBalance(Player player, String input) {
        if (InputUtils.isInputMoney(input)) {
            double moneyDeposit = InputUtils.convertToMoneyValue(input);

            if (EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), moneyDeposit)) {
                addBankBalance(player.getUniqueId(), moneyDeposit);
                MessagesManager.sendMessage(player, Component.text("Tu as transféré §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(moneyDeposit) + "§r" + EconomyManager.getEconomyIcon() + " à ta banque"), Prefix.CITY, MessageType.ERROR, false);
            } else {
                MessagesManager.sendMessage(player, MessagesManager.Message.MONEYPLAYERMISSING.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.CITY, MessageType.ERROR, true);
        }
    }

    public void withdrawBankBalance(Player player, String input) {
        if (InputUtils.isInputMoney(input)) {
            double moneyDeposit = InputUtils.convertToMoneyValue(input);

            if (getBankBalance(player.getUniqueId()) < moneyDeposit) {
                MessagesManager.sendMessage(player, Component.text("Tu n'a pas assez d'argent en banque"), Prefix.CITY, MessageType.ERROR, false);
            } else {
                withdrawBankBalance(player.getUniqueId(), moneyDeposit);
                EconomyManager.getInstance().addBalance(player.getUniqueId(), moneyDeposit);
                MessagesManager.sendMessage(player, Component.text("§d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(moneyDeposit) + "§r" + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"), Prefix.CITY, MessageType.SUCCESS, false);
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.CITY, MessageType.ERROR, true);
        }
    }

    private Map<UUID, Double> loadAllBanks() {
        try {
            Map<UUID, Double> banks = new HashMap<>();

            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("SELECT player, balance FROM banks");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UUID player = UUID.fromString(resultSet.getString("player"));
                double balance = resultSet.getDouble("balance");
                banks.put(player, balance);
            }

            statement.close();
            return banks;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadPlayerBank(UUID player) {
        try {
            final Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT balance FROM banks WHERE player = ?");
            statement.setString(1, player.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                banks.put(player, resultSet.getDouble("balance"));
                return;
            }

            banks.put(player, Double.parseDouble("0"));
            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                try {
                    PreparedStatement newStatement = connection.prepareStatement("INSERT INTO banks (player) VALUES (?)");
                    newStatement.setString(1, player.toString());

                    newStatement.executeUpdate();
                    newStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void savePlayerBank(UUID player) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE banks SET balance = ? WHERE player = ?");
                statement.setDouble(1, banks.get(player));
                statement.setString(2, player.toString());

                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
