package fr.openmc.core.features.economy;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.DatabaseFeature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.economy.commands.Baltop;
import fr.openmc.core.features.economy.commands.History;
import fr.openmc.core.features.economy.commands.Money;
import fr.openmc.core.features.economy.commands.Pay;
import fr.openmc.core.features.economy.models.EconomyPlayer;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@Credit(developers = {"Axeno", "Piquel Chips", "PuppyTransGirl", "Gyro"})
public class EconomyManager extends Feature implements DatabaseFeature, HasCommands {
    private static Map<UUID, EconomyPlayer> balances;

    private static Dao<EconomyPlayer, String> playersDao;
    private static final Set<UUID> dirtyBalances = new HashSet<>();
    private static final Object balancesLock = new Object();
    private static final Object saveLock = new Object();
    private static final long AUTO_SAVE_INTERVAL_TICKS = 20L * 60L * 5L; // 5 minutes
    private static BukkitTask autoSaveTask;

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>(Map.of(
            1_000L, "k",
            1_000_000L, "M",
            1_000_000_000L, "B",
            1_000_000_000_000L, "T",
            1_000_000_000_000_000L, "Qa",
            1_000_000_000_000_000_000L, "Qi"));

    @Override
    public void init() {
        balances = loadAllBalances();
        dirtyBalances.clear();
        startAutoSaveTask();
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
        stopAutoSaveTask();
        saveAllBalances(true);
    }

    public static double getBalance(UUID playerUUID) {
        synchronized (balancesLock) {
            EconomyPlayer bank = balances.get(playerUUID);
            return bank == null ? 0 : bank.getBalance();
        }
    }

    public static Map<UUID, EconomyPlayer> getBalances() {
        synchronized (balancesLock) {
            Map<UUID, EconomyPlayer> snapshot = new HashMap<>();

            balances.forEach((playerUUID, player) -> snapshot.put(playerUUID, copyPlayer(player)));

            return Collections.unmodifiableMap(snapshot);
        }
    }

    public static void addBalance(UUID playerUUID, double amount) {
        addBalance(playerUUID, amount, null);
    }

    public static void addBalance(UUID playerUUID, double amount, @Nullable String reason) {
        synchronized (balancesLock) {
            EconomyPlayer bank = getOrCreatePlayerBank(playerUUID);
            bank.deposit(amount);
            markPlayerBankDirty(bank);
        }

        if (reason != null) {
            TransactionsManager.registerTransaction(new Transaction(
                playerUUID.toString(),
                "CONSOLE",
                amount,
                reason
            ));
        }

    }

    public static boolean withdrawBalance(UUID playerUUID, double amount) {
        return withdrawBalance(playerUUID, amount, null);
    }

    public static boolean withdrawBalance(UUID playerUUID, double amount, @Nullable String reason) {
        synchronized (balancesLock) {
            EconomyPlayer bank = getOrCreatePlayerBank(playerUUID);

            if (!bank.withdraw(amount)) {
                return false;
            }

            markPlayerBankDirty(bank);
        }

        if (reason != null) {
            TransactionsManager.registerTransaction(new Transaction(
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
    public static boolean transferBalance(UUID fromPlayer, UUID toPlayer, double amount) {
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
    public static boolean transferBalance(UUID fromPlayer, UUID toPlayer, double amount, @Nullable String reason) {
        if (withdrawBalance(fromPlayer, amount)) {
            addBalance(toPlayer, amount);

            if (reason != null) {
                TransactionsManager.registerTransaction(new Transaction(
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

    public static void setBalance(UUID playerUUID, double amount) {
        synchronized (balancesLock) {
            EconomyPlayer bank = getOrCreatePlayerBank(playerUUID);
            bank.setBalance(amount);
            markPlayerBankDirty(bank);
        }
    }

    public static String getMiniBalance(UUID playerUUID) {
        double balance = getBalance(playerUUID);

        return getFormattedSimplifiedNumber(balance);
    }

    public static void markPlayerBankDirty(EconomyPlayer player) {
        synchronized (balancesLock) {
            balances.put(player.getPlayerUUID(), copyPlayer(player));
            dirtyBalances.add(player.getPlayerUUID());
        }
    }

    /**
     * Returns a snapshot of a player's economy data.
     * <p>
     * Mutating the returned {@link EconomyPlayer} does not update the cache or mark
     * the balance dirty. Use {@link #setBalance(UUID, double)},
     * {@link #addBalance(UUID, double)} or {@link #withdrawBalance(UUID, double)}
     * to change a player's balance.
     */
    public static EconomyPlayer getPlayerBank(UUID playerUUID) {
        synchronized (balancesLock) {
            EconomyPlayer bank = balances.get(playerUUID);
            return bank == null ? new EconomyPlayer(playerUUID) : copyPlayer(bank);
        }
    }

    private static EconomyPlayer getOrCreatePlayerBank(UUID playerUUID) {
        return balances.computeIfAbsent(playerUUID, EconomyPlayer::new);
    }

    public static void saveAllBalances() {
        saveAllBalances(false);
    }

    private static void saveAllBalances(boolean finalSave) {
        synchronized (saveLock) {
            do {
                List<EconomyPlayer> playersToSave;

                synchronized (balancesLock) {
                    if (dirtyBalances.isEmpty()) {
                        return;
                    }

                    playersToSave = dirtyBalances.stream()
                            .map(balances::get)
                            .filter(Objects::nonNull)
                            .map(EconomyManager::copyPlayer)
                            .toList();
                    dirtyBalances.clear();
                }

                try {
                    playersDao.callBatchTasks(() -> {
                        for (EconomyPlayer player : playersToSave) {
                            playersDao.createOrUpdate(player);
                        }

                        return null;
                    });
                } catch (Exception e) {
                    synchronized (balancesLock) {
                        for (EconomyPlayer player : playersToSave) {
                            dirtyBalances.add(player.getPlayerUUID());
                        }
                    }

                    if (finalSave) {
                        OMCLogger.error("CRITIQUE: Impossible de sauvegarder les soldes de l'economie pendant l'arret. Des soldes non sauvegardes peuvent etre perdus si le serveur s'arrete.", e);
                    } else {
                        OMCLogger.error("Impossible de sauvegarder les soldes de l'economie. Les soldes modifies seront reessayes a la prochaine sauvegarde.", e);
                    }

                    return;
                }
            } while (finalSave);
        }
    }

    private static EconomyPlayer copyPlayer(EconomyPlayer player) {
        return new EconomyPlayer(player.getPlayerUUID(), player.getBalance());
    }

    public static Map<UUID, EconomyPlayer> loadAllBalances() {
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

    private static void startAutoSaveTask() {
        if (OMCPlugin.getInstance() == null || OMCPlugin.isUnitTestVersion() || autoSaveTask != null) {
            return;
        }

        autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                OMCPlugin.getInstance(),
                () -> EconomyManager.saveAllBalances(),
                AUTO_SAVE_INTERVAL_TICKS,
                AUTO_SAVE_INTERVAL_TICKS
        );
    }

    private static void stopAutoSaveTask() {
        if (autoSaveTask == null) {
            return;
        }

        autoSaveTask.cancel();
        autoSaveTask = null;
    }

    public static String getFormattedBalance(UUID playerUUID) {
        String balance = String.valueOf(getBalance(playerUUID));
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
        if (ItemsAdderHook.isEnable()) {
            return FontImageWrapper.replaceFontImages("§f:aywenito:");
        } else {
            return "Ⓐ";
        }
    }
}
