package fr.openmc.core.features.economy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.h2.api.Trigger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.openmc.core.features.economy.models.EconomyPlayer;

public class EconomyManagerDirtySaveTest {
    private ConnectionSource connectionSource;
    private EconomyManager economyManager;
    private String databaseUrl;

    @BeforeEach
    public void setUp() throws Exception {
        databaseUrl = "jdbc:h2:mem:economy_dirty_save_" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1";
        connectionSource = new JdbcConnectionSource(databaseUrl);
        economyManager = new EconomyManager();

        economyManager.initDB(connectionSource);
        economyManager.init();
        createDirtyDuringSaveTriggers();
    }

    @AfterEach
    public void tearDown() throws Exception {
        DirtyDuringSaveTrigger.clear();

        if (connectionSource != null) {
            connectionSource.close();
        }
    }

    @Test
    public void testFinalSaveFlushesBalancesDirtiedDuringSave() throws Exception {
        UUID firstPlayerUUID = UUID.randomUUID();
        UUID secondPlayerUUID = UUID.randomUUID();

        DirtyDuringSaveTrigger.runOnce(() -> EconomyManager.setBalance(secondPlayerUUID, 700.0));

        EconomyManager.setBalance(firstPlayerUUID, 500.0);

        economyManager.save();

        Map<UUID, EconomyPlayer> balancesAfterSave = EconomyManager.loadAllBalances();
        assertEquals(500.0, balancesAfterSave.get(firstPlayerUUID).getBalance());
        assertEquals(700.0, balancesAfterSave.get(secondPlayerUUID).getBalance());
    }

    @Test
    public void testFinalSaveFlushesSameBalanceDirtiedDuringSave() throws Exception {
        UUID playerUUID = UUID.randomUUID();

        DirtyDuringSaveTrigger.runOnce(() -> EconomyManager.setBalance(playerUUID, 700.0));

        EconomyManager.setBalance(playerUUID, 500.0);

        economyManager.save();

        Map<UUID, EconomyPlayer> balancesAfterSave = EconomyManager.loadAllBalances();
        assertEquals(700.0, balancesAfterSave.get(playerUUID).getBalance());
    }

    private void createDirtyDuringSaveTriggers() throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl);
             Statement statement = connection.createStatement()) {
            String triggerClassName = DirtyDuringSaveTrigger.class.getName();

            statement.execute("CREATE TRIGGER dirty_during_insert BEFORE INSERT ON balances FOR EACH ROW CALL '" + triggerClassName + "'");
            statement.execute("CREATE TRIGGER dirty_during_update BEFORE UPDATE ON balances FOR EACH ROW CALL '" + triggerClassName + "'");
        }
    }

    public static final class DirtyDuringSaveTrigger implements Trigger {
        private static final AtomicReference<Runnable> callback = new AtomicReference<>();
        private static final AtomicBoolean callbackInvoked = new AtomicBoolean(false);

        static void runOnce(Runnable callback) {
            DirtyDuringSaveTrigger.callback.set(callback);
            callbackInvoked.set(false);
        }

        static void clear() {
            callback.set(null);
            callbackInvoked.set(false);
        }

        @Override
        public void init(Connection connection, String schemaName, String triggerName, String tableName, boolean before, int type) {
        }

        @Override
        public void fire(Connection connection, Object[] oldRow, Object[] newRow) {
            Runnable currentCallback = callback.get();

            if (currentCallback != null && callbackInvoked.compareAndSet(false, true)) {
                currentCallback.run();
            }
        }

        @Override
        public void close() {
        }

        @Override
        public void remove() {
        }
    }
}
