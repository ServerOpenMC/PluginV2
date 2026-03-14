package fr.openmc.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaddingUtilsTest {

    @Test
    @DisplayName("Center text in given width")
    void testFormat_Centered() {
        String result = PaddingUtils.format("hi", 10);
        Assertions.assertEquals(10, result.length());
        Assertions.assertEquals("    hi    ", result);
    }

    @Test
    @DisplayName("Text longer than width returns text without extra padding")
    void testFormat_TextLongerThanWidth() {
        String result = PaddingUtils.format("hello world", 5);
        Assertions.assertEquals("hello world", result);
    }

    @Test
    @DisplayName("Text exactly equals width")
    void testFormat_ExactWidth() {
        String result = PaddingUtils.format("abcde", 5);
        Assertions.assertEquals("abcde", result);
    }

    @Test
    @DisplayName("Odd padding distributes correctly")
    void testFormat_OddPadding() {
        String result = PaddingUtils.format("ab", 5);
        Assertions.assertEquals(5, result.length());
        Assertions.assertEquals(" ab  ", result);
    }

    @Test
    @DisplayName("Empty string gets full padding")
    void testFormat_EmptyString() {
        String result = PaddingUtils.format("", 4);
        Assertions.assertEquals(4, result.length());
        Assertions.assertEquals("    ", result);
    }
}
