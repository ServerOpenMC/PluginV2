package fr.openmc.core.core.features.tpa.commandes;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.tpa.TPAQueue;
import fr.openmc.mockbukkit.ServerMock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.scheduler.ScheduledTask;
import org.mockbukkit.mockbukkit.world.WorldMock;

public class TPACommandTest {

    private ServerMock server;
    private OMCPlugin plugin;
    private WorldMock world;

    @BeforeEach
    public void setUp() {
        this.server = MockBukkit.mock(new ServerMock());
        this.world = this.server.addSimpleWorld("world");
        this.plugin = MockBukkit.load(OMCPlugin.class);
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
        Player player = server.addPlayerWithPermissions(plugin, "omc.commands.tpa");
        Player player1 = server.addPlayer();

        boolean perform = player.performCommand("tpa " + player1.getName());

        Assertions.assertTrue(perform);
        Assertions.assertTrue(TPAQueue.QUEUE.hasPendingRequest(player1));
    }

    @Test
    public void testASkCommand_HasPendingRequest() {
        Player player = server.addPlayerWithPermissions(plugin, "omc.commands.tpa");
        PlayerMock player1 = server.addPlayerWithPermissions(plugin, "omc.commands.tpa");

        boolean perform = player.performCommand("tpa " + player1.getName());

        Assertions.assertTrue(perform);

        perform = player1.performCommand("tpa " + player.getName());

        Assertions.assertTrue(perform);
        Assertions.assertFalse(TPAQueue.QUEUE.hasPendingRequest(player));
    }

    @Test
    public void testAcceptRequest() {
        PlayerMock player = server.addPlayerWithPermissions(plugin, "omc.commands.tpa");
        PlayerMock player1 = server.addPlayerWithPermissions(plugin, "omc.commands.tpa");

        player.teleport(new Location(this.world, 10, 10, 10));
        player1.teleport(new Location(this.world, 5, 5, 5));

        boolean perform = player.performCommand("tpa " + player1.getName());

        Assertions.assertTrue(perform);

        perform = player1.performCommand("tpaccept " + player.getName());

        Assertions.assertTrue(perform);
        Assertions.assertFalse(TPAQueue.QUEUE.hasPendingRequest(player1));
        server.getScheduler().performTicks(100); // For SettingType.TELEPORT_TITLE_FADE
        Assertions.assertEquals(player.getLocation(), player1.getLocation());
    }
}
