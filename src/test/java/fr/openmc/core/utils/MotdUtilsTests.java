package fr.openmc.core.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import fr.openmc.core.TestPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.*;

public class MotdUtilsTests {
    ServerMock server;
    TestPlugin plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(TestPlugin.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    private String getComponentContent(Component component) {
        return ((TextComponent) component).content();
    }

    @Test
    @DisplayName("MOTD switch")
    public void testMOTD() {
        String def = getComponentContent(server.motd());
        new MotdUtils(plugin);
        server.getScheduler().performTicks(12001L);
        Assertions.assertNotNull(getComponentContent(server.motd()), def);
    }
}
