package fr.openmc.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.MockBukkit;

public class MotdUtilsTest {

    private ServerMock server;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
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

        new MotdUtils();
        server.getScheduler().performTicks(12001L);

        Assertions.assertNotEquals(getComponentContent(server.motd()), motd);
    }

}
