package fr.openmc.core.features.economy;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mayor.perks.PerkUtils;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.sub.milestone.rewards.PlayerBankLimitRewards;
import fr.openmc.core.features.economy.commands.BankCommands;
import fr.openmc.core.features.economy.events.BankDepositEvent;
import fr.openmc.core.features.economy.models.Bank;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasDatabase;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.InputUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BankManager extends Feature implements HasDatabase {
    @Getter
    private Map<UUID, Bank> banks;
    private Dao<Bank, String> banksDao;

    private BukkitTask interestTask;

    private final EconomyManager economyManager = OMCRegistry.FEATURES.ECONOMY.get();

    @Override
    public void init() {
        banks = loadAllBanks();
        CommandsManager.getHandler().register(new BankCommands());
        updateInterestTimer();
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Bank.class);
        banksDao = DaoManager.createDao(connectionSource, Bank.class);
    }

    public double getBankBalance(UUID playerUUID) {
        Bank bank = getPlayerBank(playerUUID);
        return bank.getBalance();
    }

    public boolean deposit(UUID playerUUID, double amount) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new BankDepositEvent(playerUUID))
        );

        Bank bank = getPlayerBank(playerUUID);
        bank.deposit(amount);
        return saveBank(bank);
    }

    public boolean withdraw(UUID playerUUID, double amount) {
        Bank bank = getPlayerBank(playerUUID);

        if (bank.getBalance() < amount) {
            return false;
        }
        bank.withdraw(amount);
        return saveBank(bank);
    }

    public double getBalance(UUID playerUUID) {
        Bank bank = getPlayerBank(playerUUID);
        return bank.getBalance();
    }

    private Bank getPlayerBank(UUID playerUUID) {
        return banks.computeIfAbsent(playerUUID, Bank::new);
    }

    private boolean saveBank(Bank bank) {
        try {
            banks.put(bank.getPlayerUUID(), bank);
            banksDao.createOrUpdate(bank);
            return true;
        } catch (SQLException e) {
            OMCLogger.error("Failed to save bank " + bank.getPlayerUUID(), e);
            return false;
        }
    }

    public void deposit(UUID playerUUID, String input) {
        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(playerUUID);

        if (!InputUtils.isInputMoney(input)) {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("messages.global.invalid_input"),
                    Prefix.BANK, MessageType.ERROR, true);
            return;
        }

        double amount = InputUtils.convertToMoneyValue(input);
        City city = City.ofPlayer(playerUUID);

        if (city == null || city.getLevel() < 2) {
            MessagesManager.sendMessage(offlinePlayer,
                    TranslationManager.translation("feature.economy.bank.deposit.need_city_level_2"),
                    Prefix.BANK, MessageType.ERROR, false);
            return;
        }

        double limit = PlayerBankLimitRewards.getBankBalanceLimit(city.getLevel());
        double currentBalance = getBalance(playerUUID);

        if (currentBalance >= limit) {
            MessagesManager.sendMessage(offlinePlayer,
                    TranslationManager.translation("feature.economy.bank.deposit.limit_reached",
                            Component.text(economyManager.getFormattedNumber(limit)).color(NamedTextColor.LIGHT_PURPLE)),
                    Prefix.BANK, MessageType.ERROR, false);
            return;
        }

        double allowedAmount = Math.min(amount, limit - currentBalance);

        if (!economyManager.withdrawBalance(playerUUID, allowedAmount)) {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("feature.economy.bank.deposit.not_enough_money"),
                    Prefix.BANK, MessageType.ERROR, false);
            return;
        }

        deposit(playerUUID, allowedAmount);

        if (allowedAmount < amount) {
            MessagesManager.sendMessage(offlinePlayer,
                    TranslationManager.translation(
                            "feature.economy.bank.deposit.partial",
                            Component.text(economyManager.getFormattedNumber(allowedAmount)).color(NamedTextColor.LIGHT_PURPLE)
                    ),
                    Prefix.BANK, MessageType.ERROR, false);
        } else {
            MessagesManager.sendMessage(offlinePlayer,
                    TranslationManager.translation(
                            "feature.economy.bank.deposit.success",
                            Component.text(economyManager.getFormattedNumber(allowedAmount)).color(NamedTextColor.LIGHT_PURPLE)
                    ),
                    Prefix.BANK, MessageType.SUCCESS, false);
        }
    }

    public void withdraw(UUID playerUUID, String input) {
        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(playerUUID);

        if (!InputUtils.isInputMoney(input)) {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("messages.global.invalid_input"),
                    Prefix.BANK, MessageType.ERROR, true);
            return;
        }

        double amount = InputUtils.convertToMoneyValue(input);

        if (!withdraw(playerUUID, amount)) {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("feature.economy.bank.withdraw.not_enough"), Prefix.BANK, MessageType.ERROR, false);
            return;
        }

        economyManager.addBalance(playerUUID, amount, "Retrait banque personnelle");

        MessagesManager.sendMessage(offlinePlayer,
                TranslationManager.translation(
                        "feature.economy.bank.withdraw.transferred",
                        Component.text(economyManager.getFormattedSimplifiedNumber(amount)).color(NamedTextColor.LIGHT_PURPLE),
                        Component.text(economyManager.getEconomyIcon())
                ),
                Prefix.BANK, MessageType.SUCCESS, false);
    }

    private Map<UUID, Bank> loadAllBanks() {
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
    public double calculatePlayerInterest(UUID playerUUID) {
        double interest = .01; // base interest is 1%

        City city = City.ofPlayer(playerUUID);
        if (city != null && city.getMayorManager().phaseMayor == 2) {
            if (PerkUtils.hasPerk(city.getMayor(), Perks.BUSINESS_MAN.getId())) {
                interest += .02; // interest is +2% when perk Business Man enabled
            }
        }

        return interest;
    }

    public void applyPlayerInterest(UUID playerUUID) {
        double interest = calculatePlayerInterest(playerUUID);
        double amount = getBankBalance(playerUUID) * interest;

        City city = City.ofPlayer(playerUUID);
        if (city == null) return;


        double allowedAmount = Math.min(amount, Math.max(0, PlayerBankLimitRewards.getBankBalanceLimit(city.getLevel()) - getBankBalance(playerUUID)));
        if (allowedAmount <= 0) return;

        deposit(playerUUID, allowedAmount);

        Player sender = Bukkit.getPlayer(playerUUID);
        if (sender != null)
            MessagesManager.sendMessage(sender,
                    TranslationManager.translation(
                            "feature.economy.bank.interest.received",
                            Component.text(interest * 100 + "%").color(NamedTextColor.LIGHT_PURPLE),
                            Component.text(economyManager.getFormattedSimplifiedNumber(allowedAmount)).color(NamedTextColor.LIGHT_PURPLE),
                            Component.text(economyManager.getEconomyIcon())
                    ),
                    Prefix.CITY, MessageType.SUCCESS, false);
    }

    // WARNING: THIS FUNCTION IS VERY EXPENSIVE DO NOT RUN FREQUENTLY IT WILL AFFECT
    // PERFORMANCE IF THERE ARE MANY BANKS SAVED IN THE DB
    public void applyAllPlayerInterests() {
        banks = loadAllBanks();
        for (UUID player : banks.keySet()) {
            applyPlayerInterest(player);
        }
    }

    public void updateInterestTimer() {
        if (OMCPlugin.isUnitTestVersion()) return;

        if (interestTask != null) return;

        long delay = getSecondsUntilInterest() * 20L;

        interestTask = Bukkit.getScheduler().runTaskLater(
                OMCPlugin.getInstance(),
                () -> {
                    OMCLogger.info("Applying all player interests...");
                    applyAllPlayerInterests();
                    OMCRegistry.CITY_FEATURES.CITY_BANK.applyAllCityInterests();
                    OMCLogger.info("All player interests applied successfully.");

                    interestTask = null;

                    Bukkit.getScheduler().runTaskLater(
                            OMCPlugin.getInstance(),
                            this::updateInterestTimer,
                            20L * 10
                    );
                },
                delay
        );
    }

    public long getSecondsUntilInterest() {
        LocalDateTime now = DateUtils.getLocalDateTime();
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
