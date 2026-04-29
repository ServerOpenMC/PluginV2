package fr.openmc.core.features.economy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import fr.openmc.core.OMCPlugin;
import fr.openmc.mock.MockBukkitHelper;
import fr.openmc.mock.ServerMock;

public class EconomyManagerTest {
    private ServerMock server;

    private PlayerMock player1;
    private PlayerMock player2;

    @BeforeEach
    public void setUp() {
        this.server = MockBukkitHelper.safeMock();

        this.player1 = this.server.addPlayer("Player1");
        this.player2 = this.server.addPlayer("Player2");

        MockBukkit.load(OMCPlugin.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkitHelper.safeUnmock();
    }

    @Test
    public void testAddBalance() {
        EconomyManager.addBalance(player1.getUniqueId(), 100.0);
        
        assertEquals(EconomyManager.getBalance(player1.getUniqueId()), 100.0);
    }

    @Test
    public void testSuccessWithdrawBalance() {
        EconomyManager.setBalance(player1.getUniqueId(), 200.0);

        boolean success = EconomyManager.withdrawBalance(player1.getUniqueId(), 50.0);

        assertTrue(success);
        assertEquals(EconomyManager.getBalance(player1.getUniqueId()), 150.0);
    }

    @Test
    public void testFailWithdrawBalance() {
        EconomyManager.setBalance(player1.getUniqueId(), 30.0);

        boolean success = EconomyManager.withdrawBalance(player1.getUniqueId(), 50.0);

        assertFalse(success);
        assertEquals(EconomyManager.getBalance(player1.getUniqueId()), 30.0);
    }

    @Test
    public void testSetBalance() {
        EconomyManager.setBalance(player1.getUniqueId(), 500.0);

        assertEquals(EconomyManager.getBalance(player1.getUniqueId()), 500.0);
    }

    @Test
    public void testAddBalanceWithReasonRegistersTransaction() {
        EconomyManager.addBalance(player1.getUniqueId(), 100.0, "Test Reason");
        server.getScheduler().performTicks(20L);

        List<Transaction> transactions = TransactionsManager.getTransactionsByPlayers(player1.getUniqueId());
        boolean found = transactions.stream().anyMatch(t -> 
            t.recipient.equals(player1.getUniqueId().toString()) &&
            t.amount == 100.0 &&
            t.reason.equals("Test Reason")
        );

        assertTrue(found);
    }

    @Test
    public void testWithdrawBalanceWithReasonRegistersTransaction() {
        EconomyManager.setBalance(player1.getUniqueId(), 200.0);
        EconomyManager.withdrawBalance(player1.getUniqueId(), 50.0, "Withdrawal Reason");
        server.getScheduler().performTicks(20L);

        List<Transaction> transactions = TransactionsManager.getTransactionsByPlayers(player1.getUniqueId());

        boolean found = transactions.stream().anyMatch(t -> 
            t.sender.equals(player1.getUniqueId().toString()) &&
            t.amount == 50.0 &&
            t.reason.equals("Withdrawal Reason")
        );

        assertTrue(found);
    }

    @Test
    public void testWithdrawBalanceWithoutReasonDoesNotRegisterTransaction() {
        EconomyManager.setBalance(player1.getUniqueId(), 200.0);
        EconomyManager.withdrawBalance(player1.getUniqueId(), 50.0);
        server.getScheduler().performTicks(20L);

        List<Transaction> transactions = TransactionsManager.getTransactionsByPlayers(player1.getUniqueId());
        boolean found = transactions.stream().anyMatch(t -> 
            t.sender.equals(player1.getUniqueId().toString()) &&
            t.amount == 50.0
        );

        assertFalse(found);
    }

    @Test
    public void testTransferBalance() {
        EconomyManager.setBalance(player1.getUniqueId(), 300.0);

        boolean success = EconomyManager.transferBalance(player1.getUniqueId(), player2.getUniqueId(), 100.0);

        assertTrue(success);
        assertEquals(EconomyManager.getBalance(player1.getUniqueId()), 200.0);
        assertEquals(EconomyManager.getBalance(player2.getUniqueId()), 100.0);
    }

    @Test
    public void testFailedTransferBalanceDueToInsufficientFunds() {
        EconomyManager.setBalance(player1.getUniqueId(), 50.0);

        boolean success = EconomyManager.transferBalance(player1.getUniqueId(), player2.getUniqueId(), 100.0);
        
        assertFalse(success);
        assertEquals(EconomyManager.getBalance(player1.getUniqueId()), 50.0);
        assertEquals(EconomyManager.getBalance(player2.getUniqueId()), 0.0);
    }

    @Test
    public void testTransferBalanceWithReasonRegistersTransaction() {
        EconomyManager.setBalance(player1.getUniqueId(), 400.0);
        EconomyManager.transferBalance(player1.getUniqueId(), player2.getUniqueId(), 150.0, "Gift");
        server.getScheduler().performTicks(120L);

        List<Transaction> transactions = TransactionsManager.getTransactionsByPlayers(player1.getUniqueId());

        boolean found = transactions.stream().anyMatch(t ->
            t.sender.equals(player1.getUniqueId().toString()) &&
            t.recipient.equals(player2.getUniqueId().toString()) &&
            t.amount == 150.0 &&
            t.reason.equals("Gift")
        );

        assertTrue(found);
    }
}
