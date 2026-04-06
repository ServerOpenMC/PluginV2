package fr.openmc.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class RandomUtilsTest {

    @RepeatedTest(50)
    @DisplayName("randomBetween int stays in bounds")
    void testRandomBetweenInt_InBounds() {
        int result = RandomUtils.randomBetween(5, 10);
        Assertions.assertTrue(result >= 5 && result <= 10,
                "Expected between 5 and 10, got: " + result);
    }

    @RepeatedTest(50)
    @DisplayName("randomBetween double stays in bounds")
    void testRandomBetweenDouble_InBounds() {
        double result = RandomUtils.randomBetween(1.0, 5.0);
        Assertions.assertTrue(result >= 1.0 && result < 5.0,
                "Expected between 1.0 and 5.0, got: " + result);
    }

    @RepeatedTest(50)
    @DisplayName("randomBetween float stays in bounds")
    void testRandomBetweenFloat_InBounds() {
        float result = RandomUtils.randomBetween(0.0f, 1.0f);
        Assertions.assertTrue(result >= 0.0f && result < 1.0f,
                "Expected between 0.0 and 1.0, got: " + result);
    }

    @Test
    @DisplayName("randomBetween int same min max returns that value")
    void testRandomBetweenInt_SameMinMax() {
        Assertions.assertEquals(7, RandomUtils.randomBetween(7, 7));
    }

    @RepeatedTest(50)
    @DisplayName("randomBetween int negative range")
    void testRandomBetweenInt_NegativeRange() {
        int result = RandomUtils.randomBetween(-10, -5);
        Assertions.assertTrue(result >= -10 && result <= -5,
                "Expected between -10 and -5, got: " + result);
    }
}
