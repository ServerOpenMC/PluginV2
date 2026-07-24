package fr.openmc.core.features.bits;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.bits.commands.BitsCommands;
import fr.openmc.core.features.bits.models.BitsPlayer;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;

@Credit(developers = {"iambibi_"})
public class BitsManager extends Feature implements HasDatabase, HasCommands {
    @Getter
    private static Map<UUID, BitsPlayer> bitsData;

    private static Dao<BitsPlayer, String> playersBitsDao;

    @Override
    public void init() {
        bitsData = loadAllBits();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new BitsCommands()
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

    public static Map<UUID, BitsPlayer> loadAllBits() {
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

    private static void saveAllBits() {
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

    public static double getBits(UUID playerUUID) {
        BitsPlayer bitsPlayer = bitsData.get(playerUUID);
        return bitsPlayer == null ? 0 : bitsPlayer.getBits();
    }

    public static void addBits(UUID playerUUID, double amount) {
        BitsPlayer bitsPlayer = getBitsPlayer(playerUUID);
        bitsPlayer.deposit(amount);
    }

    public static boolean withdrawBits(UUID playerUUID, double amount) {
        BitsPlayer bitsPlayer = getBitsPlayer(playerUUID);
        return bitsPlayer.withdraw(amount);
    }

    public static void setBits(UUID playerUUID, double amount) {
        BitsPlayer bitsPlayer = getBitsPlayer(playerUUID);
        bitsPlayer.setBits(amount);
    }

    public static BitsPlayer getBitsPlayer(UUID playerUUID) {
        return bitsData.computeIfAbsent(playerUUID, BitsPlayer::new);
    }

    public static String getFormattedBits(UUID playerUUID) {
        String balance = String.valueOf(getBits(playerUUID));
        Currency currency = Currency.getInstance(Locale.FRANCE);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        format.setCurrency(currency);
        BigDecimal bd = new BigDecimal(balance);
        return format.format(bd).replace(NumberFormat.getCurrencyInstance(Locale.FRANCE).getCurrency().getSymbol(),
                getBitsIcon());
    }

    public static String getBitsIcon() {
        if (ItemsAdderHook.isEnable()) {
            return FontImageWrapper.replaceFontImages("§f:bits:");
        } else {
            return "✯";
        }
    }
}
