package fr.openmc.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

class MotdUtilsTest {
    @Test
    @DisplayName("MOTD switch")
    void testMOTD() {
        String motd = getComponentContent(MockBukkit.getMock().motd());

        new MotdUtils();
        MockBukkit.getMock().getScheduler().performTicks(12001L);

        Assertions.assertNotEquals(getComponentContent(MockBukkit.getMock().motd()), motd);
    }

    private String getComponentContent(Component component) {
        return ((TextComponent) component).content();
    }
}
