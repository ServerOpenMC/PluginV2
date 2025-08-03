package fr.openmc.core;

import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

public class OMCPluginTest {

    public OMCPlugin plugin;
    public static ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();

        server.addSimpleWorld("world");

        plugin = MockBukkit.load(OMCPlugin.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test if plugin is load")
    void testPluginIsEnabled() {
        Assertions.assertTrue(plugin.isEnabled());
    }
}
