package fr.openmc.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MathUtilsTest {

    @Test
    @DisplayName("lerpColor t=0 returns start color")
    void testLerpColor_Start() {
        int start = 0xFF0000;
        int end = 0x0000FF;
        Assertions.assertEquals(start, MathUtils.lerpColor(start, end, 0.0));
    }

    @Test
    @DisplayName("lerpColor t=1 returns end color")
    void testLerpColor_End() {
        int start = 0xFF0000;
        int end = 0x0000FF;
        Assertions.assertEquals(end, MathUtils.lerpColor(start, end, 1.0));
    }

    @Test
    @DisplayName("lerpColor t=0.5 returns midpoint")
    void testLerpColor_Midpoint() {
        int start = 0x000000;
        int end = 0xFEFEFE;
        int result = MathUtils.lerpColor(start, end, 0.5);
        Assertions.assertEquals(0x7F7F7F, result);
    }

    @Test
    @DisplayName("lerpColor same colors returns same")
    void testLerpColor_SameColors() {
        int color = 0xABCDEF;
        Assertions.assertEquals(color, MathUtils.lerpColor(color, color, 0.5));
    }

    @Test
    @DisplayName("lerpColor black to white at 0.25")
    void testLerpColor_QuarterWay() {
        int result = MathUtils.lerpColor(0x000000, 0xFFFFFF, 0.25);
        int r = (result >> 16) & 0xFF;
        int g = (result >> 8) & 0xFF;
        int b = result & 0xFF;
        Assertions.assertEquals(63, r);
        Assertions.assertEquals(63, g);
        Assertions.assertEquals(63, b);
    }
}
