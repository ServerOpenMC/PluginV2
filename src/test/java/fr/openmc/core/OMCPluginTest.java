package fr.openmc.core;

import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;

import fr.openmc.mock.MockBukkitHelper;
import fr.openmc.mock.ServerMock;

public class OMCPluginTest {

    public OMCPlugin plugin;
    public ServerMock server;

    @BeforeEach
    void setUp() {
        this.server = MockBukkitHelper.safeMock();

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
