package fr.openmc.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DateUtilsTest {

    @Test
    @DisplayName("Time to Ticks")
    void testConvertTime() {
        Assertions.assertEquals(
                "20m",
                DateUtils.convertTime(24000)
        );
    }

}