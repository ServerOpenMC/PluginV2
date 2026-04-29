package fr.openmc.core.utils;

import fr.openmc.core.OMCPlugin;
import fr.openmc.mock.MockBukkitHelper;
import fr.openmc.mock.ServerMock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;

class MotdUtilsTest {

    private ServerMock server;

    @BeforeEach
    void setUp() {
        this.server = MockBukkitHelper.safeMock();

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
