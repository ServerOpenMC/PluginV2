package fr.openmc.core.utils;

import fr.openmc.core.OMCPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

class MotdUtilsTest {

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();

        server.addSimpleWorld("world");

        MockBukkit.load(OMCPlugin.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    private String getComponentContent(Component component) {
        return ((TextComponent) component).content();
    }

    @Test
    @DisplayName("MOTD switch")
    void testMOTD() {
        String motd = getComponentContent(server.motd());

        new MotdUtils();
        server.getScheduler().performTicks(12001L);

        Assertions.assertNotEquals(getComponentContent(server.motd()), motd);
    }

}
