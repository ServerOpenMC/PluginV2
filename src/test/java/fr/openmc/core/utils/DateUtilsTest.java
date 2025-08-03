package fr.openmc.core.utils;

import org.junit.jupiter.api.*;

public class DateUtilsTest {

    @Test
    @DisplayName("Time to Ticks")
    public void testConvertTime() {
        Assertions.assertEquals(
                "20m",
                DateUtils.convertTime(24000)
        );
    }

}