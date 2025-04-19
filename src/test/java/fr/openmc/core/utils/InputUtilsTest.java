package fr.openmc.core.utils;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class InputUtilsTest {

    @Test
    @DisplayName("Conversion Sign Input to Money")
    public void testConvertSignInputToMoney_ShouldGiveTheAmountInFloat() {
        Assertions.assertEquals(
                3000000.0,
                InputUtils.convertToMoneyValue("3m")
        );
        Assertions.assertEquals(
                3000.0,
                InputUtils.convertToMoneyValue("3k")
        );
        Assertions.assertEquals(
                3000000.0,
                InputUtils.convertToMoneyValue("3M")
        );
        Assertions.assertEquals(
                3000.0,
                InputUtils.convertToMoneyValue("3K")
        );
        Assertions.assertEquals(
                1.0,
                InputUtils.convertToMoneyValue("1")
        );
        Assertions.assertEquals(
                3000.0,
                InputUtils.convertToMoneyValue("3000")
        );
    }

    @ParameterizedTest
    @DisplayName("Conversion of input sign to -1")
    @ValueSource(strings = {"-3", "-1", "489y", "1.1", "4,5"})
    public void testConvertSignInputToMoney_ShouldGiveAnError(String input) {
        Assertions.assertEquals(-1, InputUtils.convertToMoneyValue(input));
    }

    @ParameterizedTest
    @DisplayName("Check is returned value is true")
    @ValueSource(strings = {"1", "3m", "3k", "3M", "3K", "3000"})
    public void testIsInputMoney_MustReturnTrue(String input) {
        Assertions.assertTrue(InputUtils.isInputMoney(input));
    }

    @ParameterizedTest
    @DisplayName("Check is returned value is false")
    @ValueSource(strings= {"0", "-3", "-1", "489y", "1.1", "4,5"})
    public void testIsInputMoney_MustReturnFalse(String input) {
        Assertions.assertFalse(InputUtils.isInputMoney(input));
    }

}
