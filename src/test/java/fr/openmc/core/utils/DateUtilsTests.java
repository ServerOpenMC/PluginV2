package fr.openmc.core.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.junit.jupiter.api.*;

public class DateUtilsTests {
    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("convertTime From Ticks")
    public void convertTime() {
        Assertions.assertEquals(
                "20m 0s",
                DateUtils.convertTime(24000)
        );
    }
}
