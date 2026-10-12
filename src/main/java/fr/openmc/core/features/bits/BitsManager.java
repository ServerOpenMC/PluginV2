package fr.openmc.core.features.bits;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.bits.commands.BitsCommands;
import fr.openmc.core.features.bits.models.BitsPlayer;
import fr.openmc.core.features.economy.utils.EconomyUtils;
import fr.openmc.core.hooks.github.GitHubHook;
import fr.openmc.core.hooks.github.models.ContributorStats;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.db.DBTable;
import fr.openmc.core.lifecycle.interfaces.db.HasDatabase;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Credit(developers = {"iambibi_"})
public class BitsManager extends Feature implements HasDatabase, HasCommands {
    private final GitHubHook gitHubHook = OMCRegistry.HOOKS.GITHUB;
    private final ItemsAdderHook itemsAdderHook = OMCRegistry.HOOKS.ITEMS_ADDER;

    @Getter
    private final Map<UUID, BitsPlayer> bitsData = new HashMap<>();

    private final DBTable<BitsPlayer, String> playersBitsTable = DBTable.of(BitsPlayer.class);

    private BukkitTask bitsUpdateTask;

    public static final double LINE_REQ = 500d;
    public static final double BITS_PER_LINE_REQ = 250d;

    @Override
    public void onEnable() {
        loadAllBits();
        updateBitsUpdateTimer();

    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new BitsCommands(this, gitHubHook)
        );
    }

    @Override
    public List<DBTable<?, ?>> getTables() {
        return List.of(playersBitsTable);
    }

    @Override
    protected void onDisable() {
        playersBitsTable.saveAll(bitsData.values());
        bitsData.clear();
    }

    private void loadAllBits() {
        bitsData.clear();
        bitsData.putAll(playersBitsTable.loadMap(BitsPlayer::getPlayerUUID));
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
        double balance = getBits(playerUUID);

        return EconomyUtils.getFormattedNumber(balance, getBitsIcon());
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
                    OMCRegistry.CITY_FEATURES.CITY_BANK.applyAllCityInterests();
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
