package fr.openmc.core.utils;

import org.junit.jupiter.api.*;

public class DateUtilsTest {

    @Test
    @DisplayName("Time to Ticks")
    public void testConvertTime() {
        Assertions.assertEquals(
                "0j 0h 20m 0s",
                DateUtils.convertTime(24000)
        );
    }

}
