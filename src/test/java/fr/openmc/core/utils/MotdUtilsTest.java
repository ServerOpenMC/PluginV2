package fr.openmc.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

public class MotdUtilsTest {

    private ServerMock server;
    private JavaPlugin plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();
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
        String motd = getComponentContent(server.motd());
    
        new MotdUtils(plugin);
        server.getScheduler().performTicks(12001L);

        Assertions.assertNotEquals(getComponentContent(server.motd()), motd);
    }

}
