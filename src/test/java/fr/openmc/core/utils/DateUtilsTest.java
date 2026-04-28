package fr.openmc.core.utils;

import fr.openmc.core.utils.text.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DateUtilsTest {

    @Test
    @DisplayName("Time to Ticks")
    void testConvertTime() {
        Assertions.assertEquals("20m", DateUtils.convertTime(24000));
    }

    @Test
    @DisplayName("Convert 0 seconds")
    void testConvertSecondToTime_Zero() {
        Assertions.assertEquals("0s", DateUtils.convertSecondToTime(0));
    }

    @Test
    @DisplayName("Convert seconds only")
    void testConvertSecondToTime_SecondsOnly() {
        Assertions.assertEquals("45s", DateUtils.convertSecondToTime(45));
    }

    @Test
    @DisplayName("Convert minutes and seconds")
    void testConvertSecondToTime_MinutesAndSeconds() {
        Assertions.assertEquals("2m 30s", DateUtils.convertSecondToTime(150));
    }

    @Test
    @DisplayName("Convert hours, minutes and seconds")
    void testConvertSecondToTime_HoursMinutesSeconds() {
        Assertions.assertEquals("1h 5m 10s", DateUtils.convertSecondToTime(3910));
    }

    @Test
    @DisplayName("Convert days, hours, minutes and seconds")
    void testConvertSecondToTime_Full() {
        Assertions.assertEquals("3j 4h 2m 38s", DateUtils.convertSecondToTime(273758));
    }

    @Test
    @DisplayName("Convert exact hours")
    void testConvertSecondToTime_ExactHours() {
        Assertions.assertEquals("2h", DateUtils.convertSecondToTime(7200));
    }

    @Test
    @DisplayName("Convert millis to time")
    void testConvertMillisToTime() {
        Assertions.assertEquals("1m 30s", DateUtils.convertMillisToTime(90000));
    }

    @Test
    @DisplayName("Convert 0 millis")
    void testConvertMillisToTime_Zero() {
        Assertions.assertEquals("0s", DateUtils.convertMillisToTime(0));
    }

    @Test
    @DisplayName("isBefore - same year, earlier week")
    void testIsBefore_SameYearEarlierWeek() {
        Assertions.assertTrue(DateUtils.isBefore("2025-10", "2025-20"));
    }

    @Test
    @DisplayName("isBefore - same year, same week")
    void testIsBefore_SameWeek() {
        Assertions.assertTrue(DateUtils.isBefore("2025-10", "2025-10"));
    }

    @Test
    @DisplayName("isBefore - same year, later week")
    void testIsBefore_SameYearLaterWeek() {
        Assertions.assertFalse(DateUtils.isBefore("2025-20", "2025-10"));
    }

    @Test
    @DisplayName("isBefore - earlier year")
    void testIsBefore_EarlierYear() {
        Assertions.assertTrue(DateUtils.isBefore("2024-50", "2025-1"));
    }

    @Test
    @DisplayName("isBefore - later year")
    void testIsBefore_LaterYear() {
        Assertions.assertFalse(DateUtils.isBefore("2026-1", "2025-50"));
    }
}
