package fr.openmc.core.features.economy;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.commands.Baltop;
import fr.openmc.core.features.economy.commands.History;
import fr.openmc.core.features.economy.commands.Money;
import fr.openmc.core.features.economy.commands.Pay;
import fr.openmc.core.features.economy.models.EconomyPlayer;
import fr.openmc.core.features.economy.utils.EconomyUtils;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasDatabase;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@Credit(developers = {"Axeno", "Piquel Chips", "PuppyTransGirl", "Gyro"})
public class EconomyManager extends Feature implements HasDatabase, HasCommands {
    @Getter
    private Map<UUID, EconomyPlayer> balances;

    private Dao<EconomyPlayer, String> playersDao;

    private final TransactionsManager transactionsManager = OMCRegistry.FEATURES.TRANSACTIONS.get();

    @Override
    public void init() {
        balances = loadAllBalances();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new Pay(),
                new Baltop(),
                new History(),
                new Money()
        );
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, EconomyPlayer.class);
        playersDao = DaoManager.createDao(connectionSource, EconomyPlayer.class);
    }

    @Override
    protected void save() {
        saveAllBalances();
    }

    public double getBalance(UUID playerUUID) {
        EconomyPlayer bank = balances.get(playerUUID);
        return bank == null ? 0 : bank.getBalance();
    }

    public void addBalance(UUID playerUUID, double amount) {
        addBalance(playerUUID, amount, null);
    }

    public void addBalance(UUID playerUUID, double amount, @Nullable String reason) {
        EconomyPlayer bank = getPlayerBank(playerUUID);
        bank.deposit(amount);

        if (reason != null) {
            transactionsManager.registerTransaction(new Transaction(
                playerUUID.toString(),
                "CONSOLE",
                amount,
                reason
            ));
        }

    }

    public boolean withdrawBalance(UUID playerUUID, double amount) {
        return withdrawBalance(playerUUID, amount, null);
    }

    public boolean withdrawBalance(UUID playerUUID, double amount, @Nullable String reason) {
        EconomyPlayer bank = getPlayerBank(playerUUID);

        if (!bank.withdraw(amount)) return false;

        if (reason != null) {
            transactionsManager.registerTransaction(new Transaction(
                "CONSOLE",
                playerUUID.toString(),
                amount,
                reason
            ));
        }

        return true;
    }

    /**
     * Transfer balance from one player to another
     * 
     * @param fromPlayer UUID of the player to withdraw from
     * @param toPlayer   UUID of the player to add to
     * @param amount     Amount to transfer
     * @return true if the transfer was successful, false otherwise
     */
    public boolean transferBalance(UUID fromPlayer, UUID toPlayer, double amount) {
        return transferBalance(fromPlayer, toPlayer, amount, null);
    }

    /**
     * Transfer balance from one player to another
     * 
     * @param fromPlayer UUID of the player to withdraw from
     * @param toPlayer   UUID of the player to add to
     * @param amount     Amount to transfer
     * @param reason     Reason for the transaction
     * @return true if the transfer was successful, false otherwise
     */
    public boolean transferBalance(UUID fromPlayer, UUID toPlayer, double amount, @Nullable String reason) {
        if (withdrawBalance(fromPlayer, amount)) {
            addBalance(toPlayer, amount);

            if (reason != null) {
                transactionsManager.registerTransaction(new Transaction(
                    toPlayer.toString(),
                    fromPlayer.toString(),
                    amount,
                    reason
                ));
            }

            return true;
        }

        return false;
    }

    public void setBalance(UUID playerUUID, double amount) {
        EconomyPlayer bank = getPlayerBank(playerUUID);
        bank.setBalance(amount);
    }

    public String getFormattedNumber(double number) {
        return EconomyUtils.getFormattedNumber(number, getEconomyIcon());
    }

    public String getFormattedBalance(UUID playerUUID) {
        double balance = getBalance(playerUUID);
        return EconomyUtils.getFormattedNumber(balance, getEconomyIcon());
    }

    public String getMiniBalance(UUID playerUUID) {
        double balance = getBalance(playerUUID);

        return EconomyUtils.getFormattedSimplifiedNumber(balance);
    }

    public EconomyPlayer getPlayerBank(UUID playerUUID) {
        return balances.computeIfAbsent(playerUUID, EconomyPlayer::new);
    }

    private void saveAllBalances() {
        try {
            playersDao.callBatchTasks(() -> {
                for (EconomyPlayer player : balances.values()) {
                    playersDao.createOrUpdate(player);
                }

                return null;
            });
        } catch (Exception e) {
            OMCLogger.error("Impossible de sauvegarder les soldes de l'economie pendant l'arret.", e);
        }
    }

    public Map<UUID, EconomyPlayer> loadAllBalances() {
        Map<UUID, EconomyPlayer> balances = new HashMap<>();
        try {
            List<EconomyPlayer> dbBalances = playersDao.queryForAll();
            for (EconomyPlayer bank : dbBalances) {
                balances.put(bank.getPlayerUUID(), bank);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return balances;
    }

    public String getEconomyIcon() {
        if (OMCRegistry.HOOKS.ITEMS_ADDER.isEnable()) {
            return FontImageWrapper.replaceFontImages("§f:aywenito:");
        } else {
            return "Ⓐ";
        }
    }

    public boolean hasEnoughMoney(@NotNull UUID uniqueId, int requiredAmount) {
        double balance = this.getBalance(uniqueId);
        return balance >= requiredAmount;
    }
}
