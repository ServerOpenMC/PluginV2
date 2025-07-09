package fr.openmc.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.MockBukkit;

public class OMCPluginTest {

    public OMCPlugin plugin;
    @Getter
    public static ServerMock server;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();

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
