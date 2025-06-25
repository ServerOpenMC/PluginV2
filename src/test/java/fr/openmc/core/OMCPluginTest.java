package fr.openmc.core;

import fr.openmc.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockbukkit.mockbukkit.MockBukkit;

public class OMCPluginTest {

    public OMCPlugin plugin;
    public ServerMock server;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock(new ServerMock());

        server.addSimpleWorld("world");

        plugin = MockBukkit.load(OMCPlugin.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test if plugin is load")
    public void testPluginIsEnabled() {
        Assertions.assertTrue(plugin.isEnabled());
    }
}
