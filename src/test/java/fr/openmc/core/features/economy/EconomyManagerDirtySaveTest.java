package fr.openmc.core.features.economy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.j256.ormlite.dao.Dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.openmc.core.features.economy.models.EconomyPlayer;

public class EconomyManagerDirtySaveTest {
    private Dao<EconomyPlayer, String> previousPlayersDao;
    private Map<UUID, EconomyPlayer> previousBalances;
    private Set<UUID> previousDirtyBalances;

    @BeforeEach
    public void setUp() throws Exception {
        previousPlayersDao = replacePlayersDao(null);
        previousBalances = replaceBalances(new HashMap<>());
        previousDirtyBalances = new HashSet<>(getDirtyBalances());

        getDirtyBalances().clear();
    }

    @AfterEach
    public void tearDown() throws Exception {
        replacePlayersDao(previousPlayersDao);
        replaceBalances(previousBalances);

        Set<UUID> dirtyBalances = getDirtyBalances();
        dirtyBalances.clear();
        dirtyBalances.addAll(previousDirtyBalances);
    }

    @Test
    public void testFinalSaveFlushesBalancesDirtiedDuringSave() throws Exception {
        UUID firstPlayerUUID = UUID.randomUUID();
        UUID secondPlayerUUID = UUID.randomUUID();
        Map<UUID, Double> persistedBalances = new HashMap<>();
        AtomicBoolean dirtiedDuringSave = new AtomicBoolean(false);

        replacePlayersDao(createPlayersDao(persistedBalances, () -> {
            if (dirtiedDuringSave.compareAndSet(false, true)) {
                EconomyManager.setBalance(secondPlayerUUID, 700.0);
            }
        }));

        EconomyManager.setBalance(firstPlayerUUID, 500.0);

        saveAllBalances(true);

        assertEquals(500.0, persistedBalances.get(firstPlayerUUID));
        assertEquals(700.0, persistedBalances.get(secondPlayerUUID));
    }

    @Test
    public void testFinalSaveFlushesSameBalanceDirtiedDuringSave() throws Exception {
        UUID playerUUID = UUID.randomUUID();
        Map<UUID, Double> persistedBalances = new HashMap<>();
        AtomicBoolean dirtiedDuringSave = new AtomicBoolean(false);

        replacePlayersDao(createPlayersDao(persistedBalances, () -> {
            if (dirtiedDuringSave.compareAndSet(false, true)) {
                EconomyManager.setBalance(playerUUID, 700.0);
            }
        }));

        EconomyManager.setBalance(playerUUID, 500.0);

        saveAllBalances(true);

        assertEquals(700.0, persistedBalances.get(playerUUID));
    }

    @SuppressWarnings("unchecked")
    private static Dao<EconomyPlayer, String> createPlayersDao(Map<UUID, Double> persistedBalances, Runnable onCreateOrUpdate) {
        return (Dao<EconomyPlayer, String>) Proxy.newProxyInstance(
                Dao.class.getClassLoader(),
                new Class<?>[]{Dao.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "callBatchTasks" -> ((Callable<?>) args[0]).call();
                    case "createOrUpdate" -> {
                        onCreateOrUpdate.run();
                        EconomyPlayer player = (EconomyPlayer) args[0];
                        persistedBalances.put(player.getPlayerUUID(), player.getBalance());
                        yield null;
                    }
                    case "toString" -> "InMemoryEconomyPlayerDao";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> null;
                }
        );
    }

    @SuppressWarnings("unchecked")
    private static Dao<EconomyPlayer, String> replacePlayersDao(Dao<EconomyPlayer, String> playersDao) throws Exception {
        Field playersDaoField = EconomyManager.class.getDeclaredField("playersDao");
        playersDaoField.setAccessible(true);

        Dao<EconomyPlayer, String> previousPlayersDao = (Dao<EconomyPlayer, String>) playersDaoField.get(null);
        playersDaoField.set(null, playersDao);

        return previousPlayersDao;
    }

    @SuppressWarnings("unchecked")
    private static Map<UUID, EconomyPlayer> replaceBalances(Map<UUID, EconomyPlayer> balances) throws Exception {
        Field balancesField = EconomyManager.class.getDeclaredField("balances");
        balancesField.setAccessible(true);

        Map<UUID, EconomyPlayer> previousBalances = (Map<UUID, EconomyPlayer>) balancesField.get(null);
        balancesField.set(null, balances);

        return previousBalances;
    }

    @SuppressWarnings("unchecked")
    private static Set<UUID> getDirtyBalances() throws Exception {
        Field dirtyBalancesField = EconomyManager.class.getDeclaredField("dirtyBalances");
        dirtyBalancesField.setAccessible(true);

        return (Set<UUID>) dirtyBalancesField.get(null);
    }

    private static void saveAllBalances(boolean finalSave) throws Exception {
        Method saveAllBalancesMethod = EconomyManager.class.getDeclaredMethod("saveAllBalances", boolean.class);
        saveAllBalancesMethod.setAccessible(true);
        saveAllBalancesMethod.invoke(null, finalSave);
    }
}
