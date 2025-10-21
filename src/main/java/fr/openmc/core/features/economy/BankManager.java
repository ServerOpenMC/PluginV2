package fr.openmc.core.features.economy;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.bank.CityBankManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.sub.milestone.rewards.PlayerBankLimitRewards;
import fr.openmc.core.features.economy.commands.BankCommands;
import fr.openmc.core.features.economy.events.BankDepositEvent;
import fr.openmc.core.features.economy.models.Bank;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class BankManager {
    @Getter
    private static Map<UUID, Bank> banks;

    private static Dao<Bank, String> banksDao;

    public static void init() {
        banks = loadAllBanks();
        CommandsManager.getHandler().register(new BankCommands());
        updateInterestTimer();
    }

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Bank.class);
        banksDao = DaoManager.createDao(connectionSource, Bank.class);
    }

    public static double getBankBalance(UUID playerUUID) {
        Bank bank = getPlayerBank(playerUUID);
        return bank.getBalance();
    }

    public static boolean deposit(UUID playerUUID, double amount) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new BankDepositEvent(playerUUID))
        );

        Bank bank = getPlayerBank(playerUUID);
        bank.deposit(amount);
        return saveBank(bank);
    }

    public static boolean withdraw(UUID playerUUID, double amount) {
        Bank bank = getPlayerBank(playerUUID);

        if (bank.getBalance() < amount) {
            return false;
        }
        bank.withdraw(amount);
        return saveBank(bank);
    }

    public static double getBalance(UUID playerUUID) {
        Bank bank = getPlayerBank(playerUUID);
        return bank.getBalance();
    }

    private static Bank getPlayerBank(UUID playerUUID) {
        return banks.computeIfAbsent(playerUUID, Bank::new);
    }

    private static boolean saveBank(Bank bank) {
        try {
            banks.put(bank.getPlayerUUID(), bank);
            banksDao.createOrUpdate(bank);
            return true;
        } catch (SQLException e) {
            OMCPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to save bank " + bank.getPlayerUUID(), e);
            return false;
        }
    }

    public static void deposit(UUID playerUUID, String input) {
        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(playerUUID);

        if (!InputUtils.isInputMoney(input)) {
            MessagesManager.sendMessage(offlinePlayer, Component.text("Veuillez mettre une entrée correcte"),
                    Prefix.BANK, MessageType.ERROR, true);
            return;
        }

        double amount = InputUtils.convertToMoneyValue(input);
        City city = CityManager.getPlayerCity(playerUUID);

        if (city == null || city.getLevel() < 2) {
            MessagesManager.sendMessage(offlinePlayer,
                    Component.text("Pour avoir une banque personnelle, vous devez appartenir à une ville de niveau 2 minimum !"),
                    Prefix.BANK, MessageType.ERROR, false);
            return;
        }

        double limit = PlayerBankLimitRewards.getBankBalanceLimit(city.getLevel());
        double currentBalance = getBalance(playerUUID);

        if (currentBalance >= limit) {
            MessagesManager.sendMessage(offlinePlayer,
                    Component.text("Vous avez atteint la limite de votre plafond qui est de " +
                            EconomyManager.getFormattedNumber(limit) +
                            ". Améliorez votre ville au niveau supérieur !"),
                    Prefix.BANK, MessageType.ERROR, false);
            return;
        }

        double allowedAmount = Math.min(amount, limit - currentBalance);

        if (!EconomyManager.withdrawBalance(playerUUID, allowedAmount)) {
            MessagesManager.sendMessage(offlinePlayer, Component.text("Vous n'avez pas assez d'argent"),
                    Prefix.BANK, MessageType.ERROR, false);
            return;
        }

        deposit(playerUUID, allowedAmount);

        if (allowedAmount < amount) {
            MessagesManager.sendMessage(offlinePlayer,
                    Component.text("Seulement " + EconomyManager.getFormattedNumber(allowedAmount) +
                            " ont été déposés (plafond atteint)."),
                    Prefix.BANK, MessageType.ERROR, false);
        } else {
            MessagesManager.sendMessage(offlinePlayer,
                    Component.text("Vous avez déposé " +
                            EconomyManager.getFormattedNumber(allowedAmount) + "."),
                    Prefix.BANK, MessageType.SUCCESS, false);
        }
    }

    public static void withdraw(UUID playerUUID, String input) {
        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(playerUUID);

        if (!InputUtils.isInputMoney(input)) {
            MessagesManager.sendMessage(offlinePlayer, Component.text("Veuillez mettre une entrée correcte"),
                    Prefix.BANK, MessageType.ERROR, true);
            return;
        }

        double amount = InputUtils.convertToMoneyValue(input);

        if (!withdraw(playerUUID, amount)) {
            MessagesManager.sendMessage(offlinePlayer,
                    Component.text("Tu n'as pas assez d'argent en banque"),
                    Prefix.BANK, MessageType.ERROR, false);
            return;
        }

        EconomyManager.addBalance(playerUUID, amount);

        MessagesManager.sendMessage(offlinePlayer,
                Component.text("§d" + EconomyManager.getFormattedSimplifiedNumber(amount) + "§r"
                        + EconomyManager.getEconomyIcon() + " ont été transférés à votre compte"),
                Prefix.BANK, MessageType.SUCCESS, false);
    }

    private static Map<UUID, Bank> loadAllBanks() {
        Map<UUID, Bank> newBanks = new HashMap<>();
        try {
            List<Bank> dbBanks = banksDao.queryForAll();
            for (Bank bank : dbBanks) {
                newBanks.put(bank.getPlayerUUID(), bank);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newBanks;
    }

    // Interests calculated as proportion not percentage (eg: 0.01 = 1%)
    public static double calculatePlayerInterest(UUID playerUUID) {
        double interest = .01; // base interest is 1%

        if (MayorManager.phaseMayor == 2) {
            if (PerkManager.hasPerk(CityManager.getPlayerCity(playerUUID).getMayor(), Perks.BUSINESS_MAN.getId())) {
                interest += .02; // interest is +2% when perk Business Man enabled
            }
        }

        return interest;
    }

    public static void applyPlayerInterest(UUID playerUUID) {
        double interest = calculatePlayerInterest(playerUUID);
        double amount = getBankBalance(playerUUID) * interest;

        City city = CityManager.getPlayerCity(playerUUID);
        if (city == null) return;


        double allowedAmount = Math.min(amount, Math.max(0, PlayerBankLimitRewards.getBankBalanceLimit(city.getLevel()) - getBankBalance(playerUUID)));
        if (allowedAmount <= 0) return;

        deposit(playerUUID, allowedAmount);

        Player sender = Bukkit.getPlayer(playerUUID);
        if (sender != null)
            MessagesManager.sendMessage(sender,
                    Component.text("Vous venez de percevoir §d" + interest * 100 + "% §rd'intérêt, soit §d"
                            + EconomyManager.getFormattedSimplifiedNumber(allowedAmount) + "§r"
                            + EconomyManager.getEconomyIcon()),
                    Prefix.CITY, MessageType.SUCCESS, false);
    }

    // WARNING: THIS FUNCTION IS VERY EXPENSIVE DO NOT RUN FREQUENTLY IT WILL AFFECT
    // PERFORMANCE IF THERE ARE MANY BANKS SAVED IN THE DB
    public static void applyAllPlayerInterests() {
        banks = loadAllBanks();
        for (UUID player : banks.keySet()) {
            applyPlayerInterest(player);
        }
    }

    private static void updateInterestTimer() {
        if (OMCPlugin.isUnitTestVersion()) return; // Cette méthode bloque totalement le flux des tests. Si
        // quelqu'un fait les unit test des banques, merci de le prendre en compte.
        
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            OMCPlugin.getInstance().getSLF4JLogger().info("Applying all player interests...");
            applyAllPlayerInterests();
            CityBankManager.applyAllCityInterests();
            OMCPlugin.getInstance().getSLF4JLogger().info("All player interests applied successfully.");
            updateInterestTimer();

        }, getSecondsUntilInterest() * 20); // 20 ticks per second (ideally)
    }

    public static long getSecondsUntilInterest() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).withHour(2).withMinute(0)
                .withSecond(0);
        // if it is after 2 AM, get the monday after
        if (nextMonday.isBefore(now))
            nextMonday = nextMonday.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(2).withMinute(0)
                    .withSecond(0);

        LocalDateTime nextThursday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY)).withHour(2)
                .withMinute(0).withSecond(0);
        // if it is after 2 AM, get the thursday after
        if (nextThursday.isBefore(now))
            nextThursday = nextThursday.with(TemporalAdjusters.next(DayOfWeek.THURSDAY)).withHour(2).withMinute(0)
                    .withSecond(0);

        LocalDateTime nextInterestUpdate = nextMonday.isBefore(nextThursday) ? nextMonday : nextThursday;

        return ChronoUnit.SECONDS.between(now, nextInterestUpdate);
    }
}
