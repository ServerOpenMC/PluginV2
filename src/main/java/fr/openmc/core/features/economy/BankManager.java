package fr.openmc.core.features.economy;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import fr.openmc.core.features.economy.commands.BankCommands;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.features.economy.models.Bank;

public class BankManager {
    @Getter
    private static Map<UUID, Bank> banks;

    private static Dao<Bank, String> banksDao;

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Bank.class);
        banksDao = DaoManager.createDao(connectionSource, Bank.class);
    }

    public BankManager() {
        banks = loadAllBanks();
        CommandsManager.getHandler().register(new BankCommands());
        updateInterestTimer();
    }

    public static double getBankBalance(UUID player) {
        Bank bank = getPlayerBank(player);
        return bank.getBalance();
    }

    public static void addBankBalance(UUID player, double amount) {
        Bank bank = getPlayerBank(player);
        
        bank.deposit(amount);
        saveBank(bank);
    }

    public static void withdrawBankBalance(UUID player, double amount) {
        Bank bank = getPlayerBank(player);
        
        bank.withdraw(amount);
        saveBank(bank);
    }

    public static void addBankBalance(Player player, String input) {
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

    public static void withdrawBankBalance(Player player, String input) {
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

    private static Bank getPlayerBank(UUID player) {
        Bank bank = banks.get(player);
        if (bank != null) return bank;
        return new Bank(player.toString());
    }

    private static void saveBank(Bank bank) {
        try {
            banksDao.createOrUpdate(bank);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<UUID, Bank> loadAllBanks() {
        Map<UUID, Bank> newBanks = new HashMap<>();
        try {
            List<Bank> dbBanks = banksDao.queryForAll();
            for (Bank bank : dbBanks) {
                newBanks.put(bank.getPlayer(), bank);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newBanks;
    }

    // Interests calculated as proportion not percentage (eg: 0.01 = 1%)
    public static double calculatePlayerInterest(UUID player) {
        double interest = .01; // base interest is 1%

        if (MayorManager.getInstance().phaseMayor == 2) {
            if (PerkManager.hasPerk(CityManager.getPlayerCity(player).getMayor(), Perks.BUISNESS_MAN.getId())) {
                interest = .03; // interest is 3% when perk Buisness Man actived
            }
        }
        
        return interest;
    }

    public static void applyPlayerInterest(UUID player) {
        double interest = calculatePlayerInterest(player);
        double amount = getBankBalance(player) * interest;
        addBankBalance(player, amount);

        Player sender = Bukkit.getPlayer(player);
        if (sender != null)
            MessagesManager.sendMessage(sender, Component.text("Vous venez de percevoir §d" + interest*100 + "% §rd'intérèt, soit §d" + EconomyManager.getFormattedSimplifiedNumber(amount) + "§r" + EconomyManager.getEconomyIcon()), Prefix.CITY, MessageType.SUCCESS, false);
    }

    // WARNING: THIS FUNCTION IS VERY EXPENSIVE DO NOT RUN FREQUENTLY IT WILL AFFECT PERFORMANCE IF THERE ARE MANY BANKS SAVED IN THE DB
    public static void applyAllPlayerInterests() {
        banks = loadAllBanks();
        for (UUID player : banks.keySet()) {
            applyPlayerInterest(player);
        }
    }

    private static void updateInterestTimer() {
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            OMCPlugin.getInstance().getLogger().info("Distribution des intérèts...");
            applyAllPlayerInterests();
            CityManager.applyAllCityInterests();
            OMCPlugin.getInstance().getLogger().info("Distribution des intérèts réussie.");
            updateInterestTimer();

        }, getSecondsUntilInterest() * 20); // 20 ticks per second (ideally)
    }

    public static long getSecondsUntilInterest() {
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
