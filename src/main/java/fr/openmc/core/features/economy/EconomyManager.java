package fr.openmc.core.features.economy;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.economy.commands.Baltop;
import fr.openmc.core.features.economy.commands.History;
import fr.openmc.core.features.economy.commands.Money;
import fr.openmc.core.features.economy.commands.Pay;
import fr.openmc.core.features.economy.models.EconomyPlayer;
import fr.openmc.core.utils.api.ItemsAdderApi;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class EconomyManager {
    @Getter
    private static Map<UUID, EconomyPlayer> balances;

    private static Dao<EconomyPlayer, String> playersDao;

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, EconomyPlayer.class);
        playersDao = DaoManager.createDao(connectionSource, EconomyPlayer.class);
    }

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>(Map.of(
            1_000L, "k",
            1_000_000L, "M",
            1_000_000_000L, "B",
            1_000_000_000_000L, "T",
            1_000_000_000_000_000L, "Q",
            1_000_000_000_000_000_000L, "Qi"));

    public EconomyManager() {
        balances = loadAllBalances();

        CommandsManager.getHandler().register(
                new Pay(),
                new Baltop(),
                new History(),
                new Money());
    }

    public static double getBalance(UUID player) {
        EconomyPlayer bank = getPlayerBank(player);
        return bank.getBalance();
    }

    public static void addBalance(UUID player, double amount) {
        EconomyPlayer bank = getPlayerBank(player);
        bank.deposit(amount);
        savePlayerBank(bank);
    }

    public static boolean withdrawBalance(UUID player, double amount) {
        EconomyPlayer bank = getPlayerBank(player);
        if (bank.withdraw(amount)) {
            savePlayerBank(bank);
            return true;
        }
        return false;
    }

    public static void setBalance(UUID player, double amount) {
        EconomyPlayer bank = getPlayerBank(player);
        bank.withdraw(bank.getBalance());
        bank.deposit(amount);
        savePlayerBank(bank);
    }

    public static String getMiniBalance(UUID player) {
        double balance = getBalance(player);

        return getFormattedSimplifiedNumber(balance);
    }

    public static void savePlayerBank(EconomyPlayer player) {
        try {
            balances.put(player.getPlayer(), player);
            playersDao.createOrUpdate(player);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static EconomyPlayer getPlayerBank(UUID player) {
        EconomyPlayer bank = balances.get(player);
        if (bank != null)
            return bank;
        return new EconomyPlayer(player);
    }

    public static Map<UUID, EconomyPlayer> loadAllBalances() {
        Map<UUID, EconomyPlayer> balances = new HashMap<>();
        try {
            List<EconomyPlayer> dbBalances = playersDao.queryForAll();
            for (EconomyPlayer bank : dbBalances) {
                balances.put(bank.getPlayer(), bank);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return balances;
    }

    public static String getFormattedBalance(UUID player) {
        String balance = String.valueOf(getBalance(player));
        Currency currency = Currency.getInstance(Locale.FRANCE);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        format.setCurrency(currency);
        BigDecimal bd = new BigDecimal(balance);
        return format.format(bd).replace(NumberFormat.getCurrencyInstance(Locale.FRANCE).getCurrency().getSymbol(),
                getEconomyIcon());
    }

    public static String getFormattedNumber(double number) {
        Currency currency = Currency.getInstance(Locale.FRANCE);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        format.setCurrency(currency);
        BigDecimal bd = new BigDecimal(number);
        return format.format(bd).replace(NumberFormat.getCurrencyInstance(Locale.FRANCE).getCurrency().getSymbol(),
                getEconomyIcon());
    }

    public static String getFormattedSimplifiedNumber(double balance) {
        if (balance == 0) {
            return "0";
        }

        Map.Entry<Long, String> entry = suffixes.floorEntry((long) balance);
        if (entry == null) {
            return decimalFormat.format(balance);
        }

        long divideBy = entry.getKey();
        String suffix = entry.getValue();

        double truncated = balance / divideBy;
        String formatted = decimalFormat.format(truncated);

        return formatted + suffix;
    }

    public static String getEconomyIcon() {
        if (ItemsAdderApi.hasItemAdder()) {
            return FontImageWrapper.replaceFontImages("§f:aywenito:");
        } else {
            return "Ⓐ";
        }
    }
}
