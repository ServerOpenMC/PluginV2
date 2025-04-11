package fr.openmc.core.features.economy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.economy.commands.BankCommands;
import lombok.Getter;

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
