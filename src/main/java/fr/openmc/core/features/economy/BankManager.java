package fr.openmc.core.features.economy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.economy.commands.BankCommands;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.messages.MessagesManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;

public class BankManager {
    @Getter private static Map<UUID, Double> banks;
    @Getter static BankManager instance;

    public static void init_db(Connection conn) throws SQLException {
        // TODO: Create DB table
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

    public void addBankBalance(UUID player, String input) {
        if (InputUtils.isInputMoney(input)) {
            double moneyDeposit = InputUtils.convertToMoneyValue(input);

            if (EconomyManager.getInstance().withdrawBalance(player, moneyDeposit)) {
                addBankBalance(player, moneyDeposit);
                MessagesManager.sendMessage(player, Component.text("Tu as transféré §d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(moneyDeposit) + "§r" + EconomyManager.getEconomyIcon() + " à ta banque"), Prefix.CITY, MessageType.ERROR, false);
            } else {
                MessagesManager.sendMessage(player, MessagesManager.Message.MONEYPLAYERMISSING.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.CITY, MessageType.ERROR, true);
        }
    }

    public void withdrawBankBalance(UUID player, String input) {
        if (InputUtils.isInputMoney(input)) {
            double moneyDeposit = InputUtils.convertToMoneyValue(input);

            if (getBankBalance(player) < moneyDeposit) {
                MessagesManager.sendMessage(player, Component.text("Tu n'a pas assez d'argent en banque"), Prefix.CITY, MessageType.ERROR, false);
            } else {
                withdrawBankBalance(player, moneyDeposit);
                EconomyManager.getInstance().addBalance(player, moneyDeposit);
                MessagesManager.sendMessage(player, Component.text("§d" + EconomyManager.getInstance().getFormattedSimplifiedNumber(moneyDeposit) + "§r" + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"), Prefix.CITY, MessageType.SUCCESS, false);
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.CITY, MessageType.ERROR, true);
        }
    }

    private Map<UUID, Double> loadAllBanks() {
        // TODO: load all the player banks from the database
        return new HashMap<UUID,Double>();
    }

    private void loadPlayerBank(UUID player) {
        // TODO: load player bank from db or generate new one
    }

    private void savePlayerBank(UUID player) {
        // TODO: save player bank to db
    }
}
