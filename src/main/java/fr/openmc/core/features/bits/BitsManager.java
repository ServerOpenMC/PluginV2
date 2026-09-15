package fr.openmc.core.features.bits;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.bits.commands.BitsCommands;
import fr.openmc.core.features.bits.models.BitsPlayer;
import fr.openmc.core.hooks.github.GitHubHook;
import fr.openmc.core.hooks.github.models.ContributorStats;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasDatabase;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;

@Credit(developers = {"iambibi_"})
public class BitsManager extends Feature implements HasDatabase, HasCommands {
    private GitHubHook gitHubHook;
    private ItemsAdderHook itemsAdderHook;

    @Getter
    private static Map<UUID, BitsPlayer> bitsData;

    private static Dao<BitsPlayer, String> playersBitsDao;

    private static BukkitTask bitsUpdateTask;

    public static final double LINE_REQ = 500d;
    public static final double BITS_PER_LINE_REQ = 250d;

    @Override
    public void init() {
        this.gitHubHook = OMCRegistry.HOOKS.GITHUB;
        this.itemsAdderHook = OMCRegistry.HOOKS.ITEMS_ADDER;

        bitsData = loadAllBits();

        updateBitsUpdateTimer();

    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new BitsCommands(this, gitHubHook)
        );
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, BitsPlayer.class);
        playersBitsDao = DaoManager.createDao(connectionSource, BitsPlayer.class);
    }

    @Override
    protected void save() {
        saveAllBits();
    }

    private Map<UUID, BitsPlayer> loadAllBits() {
        Map<UUID, BitsPlayer> balances = new HashMap<>();
        try {
            List<BitsPlayer> dbBalances = playersBitsDao.queryForAll();
            for (BitsPlayer bank : dbBalances) {
                balances.put(bank.getPlayerUUID(), bank);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return balances;
    }

    private void saveAllBits() {
        try {
            playersBitsDao.callBatchTasks(() -> {
                for (BitsPlayer player : bitsData.values()) {
                    playersBitsDao.createOrUpdate(player);
                }

                return null;
            });
        } catch (Exception e) {
            OMCLogger.error("Impossible de sauvegarder les soldes de l'economie développeur pendant l'arret.", e);
        }
    }

    public double getBits(UUID playerUUID) {
        BitsPlayer bitsPlayer = bitsData.get(playerUUID);
        return bitsPlayer == null ? 0 : bitsPlayer.getBits();
    }

    public void addBits(UUID playerUUID, double amount) {
        BitsPlayer bitsPlayer = getBitsPlayer(playerUUID);
        bitsPlayer.deposit(amount);
    }

    public boolean withdrawBits(UUID playerUUID, double amount) {
        BitsPlayer bitsPlayer = getBitsPlayer(playerUUID);
        return bitsPlayer.withdraw(amount);
    }

    public void setBits(UUID playerUUID, double amount) {
        BitsPlayer bitsPlayer = getBitsPlayer(playerUUID);
        bitsPlayer.setBits(amount);
    }

    public BitsPlayer getBitsPlayer(UUID playerUUID) {
        return bitsData.computeIfAbsent(playerUUID, BitsPlayer::new);
    }

    public String getFormattedBits(UUID playerUUID) {
        String balance = String.valueOf(getBits(playerUUID));
        Currency currency = Currency.getInstance(Locale.FRANCE);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        format.setCurrency(currency);
        BigDecimal bd = new BigDecimal(balance);
        return format.format(bd).replace(NumberFormat.getCurrencyInstance(Locale.FRANCE).getCurrency().getSymbol(),
                getBitsIcon());
    }

    public String getBitsIcon() {
        if (itemsAdderHook.isEnable()) {
            return FontImageWrapper.replaceFontImages("§f:bits:");
        } else {
            return "✯";
        }
    }

    public void applyContributorBitsUpdate(Long githubID) {
        UUID playerLinked = gitHubHook.getPlayerLinkTo(githubID);
        if (playerLinked == null) return;

        ContributorStats stats = gitHubHook.getStats(githubID);
        if (stats == null) return;

        int lastSavedLines = getBitsPlayer(playerLinked).getLastSavedLines();
        int currentLines = stats.getTotalLines();

        if (lastSavedLines != currentLines) {
            // 500 lignes de codes = 250 bits
            double bits = ((currentLines - lastSavedLines) / 500d) * 250d;
            addBits(playerLinked, bits);
            getBitsPlayer(playerLinked).setLastSavedLines(currentLines);
        }
    }

    public void applyAllContributorBitsUpdate() {
        Map<Long, String> contributors = gitHubHook.getContributors();

        gitHubHook.fetchContributorStats();

        for (Long githubID : contributors.keySet()) {
            applyContributorBitsUpdate(githubID);
        }
    }

    public void updateBitsUpdateTimer() {
        if (OMCPlugin.isUnitTestVersion()) return;

        if (bitsUpdateTask != null) return;

        long delay = 60L * 60L * 20L; // 1 heures

        bitsUpdateTask = Bukkit.getScheduler().runTaskLater(
                OMCPlugin.getInstance(),
                () -> {
                    OMCLogger.info("Applying all bits update for contributors");
                    applyAllContributorBitsUpdate();
                    OMCRegistry.FEATURES.CITY.get().CITY_BANK.applyAllCityInterests();
                    OMCLogger.info("All bits update for contributors applied successfully.");

                    bitsUpdateTask = null;

                    Bukkit.getScheduler().runTaskLater(
                            OMCPlugin.getInstance(),
                            this::updateBitsUpdateTimer,
                            20L * 10
                    );
                },
                delay
        );
    }
}
