package fr.openmc.core.features.economy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.city.CityManager;
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

        updateInterestTimer();
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
                MessagesManager.sendMessage(player, Component.text("Tu as transféré §d" + EconomyManager.getFormattedSimplifiedNumber(moneyDeposit) + "§r" + EconomyManager.getEconomyIcon() + " à ta banque"), Prefix.BANK, MessageType.ERROR, false);
            } else {
                MessagesManager.sendMessage(player, MessagesManager.Message.MONEYPLAYERMISSING.getMessage(), Prefix.BANK, MessageType.ERROR, false);
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.BANK, MessageType.ERROR, true);
        }
    }

    public void withdrawBankBalance(Player player, String input) {
        if (InputUtils.isInputMoney(input)) {
            double moneyDeposit = InputUtils.convertToMoneyValue(input);

            if (getBankBalance(player.getUniqueId()) < moneyDeposit) {
                MessagesManager.sendMessage(player, Component.text("Tu n'a pas assez d'argent en banque"), Prefix.BANK, MessageType.ERROR, false);
            } else {
                withdrawBankBalance(player.getUniqueId(), moneyDeposit);
                EconomyManager.getInstance().addBalance(player.getUniqueId(), moneyDeposit);
                MessagesManager.sendMessage(player, Component.text("§d" + EconomyManager.getFormattedSimplifiedNumber(moneyDeposit) + "§r" + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"), Prefix.BANK, MessageType.SUCCESS, false);
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.BANK, MessageType.ERROR, true);
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

    // Interests calculated as proportion not percentage (eg: 0.01 = 1%)
    public double calculatePlayerInterest(UUID player) {
        double interest = .01; // base interest is 1%

        // TODO: link to other systems here by simply adding to the interest variable here
        
        return interest;
    }

    public void applyPlayerInterest(UUID player) {
        double interest = calculatePlayerInterest(player);
        double amount = getBankBalance(player) * interest;
        addBankBalance(player, amount);

        Player sender = Bukkit.getPlayer(player);
        if (sender != null)
            MessagesManager.sendMessage(sender, Component.text("Vous venez de percevoir §d" + interest*100 + "% §rd'intérèt, soit §d" + EconomyManager.getFormattedSimplifiedNumber(amount) + "§r" + EconomyManager.getEconomyIcon()), Prefix.CITY, MessageType.SUCCESS, false);
    }

    // WARNING: THIS FUNCTION IS VERY EXPENSIVE DO NOT RUN FREQUENTLY IT WILL AFFECT PERFORMANCE IF THERE ARE MANY BANKS SAVED IN THE DB
    public void applyAllPlayerInterests() {
        banks = loadAllBanks();
        for (UUID player : banks.keySet()) {
            applyPlayerInterest(player);
        }
    }

    private void updateInterestTimer() {
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            OMCPlugin.getInstance().getLogger().info("Distribution des intérèts...");
            applyAllPlayerInterests();
            CityManager.applyAllCityInterests();
            OMCPlugin.getInstance().getLogger().info("Distribution des intérèts réussie.");
            updateInterestTimer();

        }, getSecondsUntilInterest() * 20); // 20 ticks per second (ideally)
    }

    public long getSecondsUntilInterest() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).withHour(2).withMinute(0).withSecond(0);
        // if it is after 2 AM, get the monday after
        if (nextMonday.isBefore(now))
            nextMonday = nextMonday.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(2).withMinute(0).withSecond(0);

        LocalDateTime nextThursday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY)).withHour(2).withMinute(0).withSecond(0);
        // if it is after 2 AM, get the thursday after
        if (nextThursday.isBefore(now))
            nextThursday = nextThursday.with(TemporalAdjusters.next(DayOfWeek.THURSDAY)).withHour(2).withMinute(0).withSecond(0);

        LocalDateTime nextInterestUpdate = nextMonday.isBefore(nextThursday) ? nextMonday : nextThursday;
        
        return ChronoUnit.SECONDS.between(now, nextInterestUpdate);
    }
}
