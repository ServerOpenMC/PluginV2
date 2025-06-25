package fr.openmc.core.core.features.tpa.commandes;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.tpa.TPAQueue;
import fr.openmc.mockbukkit.ServerMock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

public class TPACommandTest {

    private ServerMock server;

    @BeforeEach
    public void setUp() {
        this.server = MockBukkit.mock(new ServerMock());

        this.server.addSimpleWorld("world");

        MockBukkit.load(OMCPlugin.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testCommandExist() {
        Assertions.assertNotNull(Bukkit.getPluginCommand("tpa"));
    }

    @Test
    public void testAskCommand() {
        Player player = server.addPlayer();

        player.setOp(true); // TODO: replace by omc.commands.tpa permission

        Player player1 = server.addPlayer();

        boolean perform = player.performCommand("tpa " + player1.getName());

        Assertions.assertTrue(perform);
        Assertions.assertTrue(TPAQueue.QUEUE.hasPendingRequest(player1));
    }

}
