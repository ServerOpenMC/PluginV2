package fr.openmc.core.utils;

import fr.openmc.core.utils.text.InputUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InputUtilsTest {

    @Test
    @DisplayName("Conversion Sign Input to Money")
    void testConvertSignInputToMoney_ShouldGiveTheAmountInFloat() {
        Assertions.assertEquals(3000000.0, InputUtils.convertToMoneyValue("3m"));
        Assertions.assertEquals(3000.0, InputUtils.convertToMoneyValue("3k"));
        Assertions.assertEquals(3000000.0, InputUtils.convertToMoneyValue("3M"));
        Assertions.assertEquals(3000.0, InputUtils.convertToMoneyValue("3K"));
        Assertions.assertEquals(1.0, InputUtils.convertToMoneyValue("1"));
        Assertions.assertEquals(3000.0, InputUtils.convertToMoneyValue("3000"));
    }

    @Test
    @DisplayName("Conversion with decimals")
    void testConvertToMoneyValue_Decimals() {
        Assertions.assertEquals(2500000.0, InputUtils.convertToMoneyValue("2.5m"));
        Assertions.assertEquals(1500.0, InputUtils.convertToMoneyValue("1.5k"));
        Assertions.assertEquals(99.99, InputUtils.convertToMoneyValue("99.99"));
    }

    @ParameterizedTest
    @DisplayName("Conversion of input sign to -1")
    @ValueSource(strings = {"-3", "-1", "489y", "4,5", "NaN", "Infinity", "-Infinity"})
    void testConvertSignInputToMoney_ShouldGiveAnError(String input) {
        Assertions.assertEquals(-1, InputUtils.convertToMoneyValue(input));
    }

    @Test
    @DisplayName("Conversion null and empty returns -1")
    void testConvertToMoneyValue_NullAndEmpty() {
        Assertions.assertEquals(-1, InputUtils.convertToMoneyValue(null));
        Assertions.assertEquals(-1, InputUtils.convertToMoneyValue(""));
    }

    @ParameterizedTest
    @DisplayName("Check is returned value is true")
    @ValueSource(strings = {"1", "3m", "3k", "3M", "3K", "3000", "2.5m", "0.5k"})
    void testIsInputMoney_MustReturnTrue(String input) {
        Assertions.assertTrue(InputUtils.isInputMoney(input));
    }

    @ParameterizedTest
    @DisplayName("Check is returned value is false")
    @ValueSource(strings = {"0", "-3", "-1", "489y", "4,5", "NaN", "Infinity", "-Infinity"})
    void testIsInputMoney_MustReturnFalse(String input) {
        Assertions.assertFalse(InputUtils.isInputMoney(input));
    }

    @Test
    @DisplayName("isInputMoney null and empty")
    void testIsInputMoney_NullAndEmpty() {
        Assertions.assertFalse(InputUtils.isInputMoney(null));
        Assertions.assertFalse(InputUtils.isInputMoney(""));
    }

    @Test
    @DisplayName("Pluralize with count > 1 adds s")
    void testPluralize_Plural() {
        Assertions.assertEquals("joueurs", InputUtils.pluralize("joueur", 5));
    }

    @Test
    @DisplayName("Pluralize with count 1 no s")
    void testPluralize_Singular() {
        Assertions.assertEquals("joueur", InputUtils.pluralize("joueur", 1));
    }

    @Test
    @DisplayName("Pluralize with count 0 no s")
    void testPluralize_Zero() {
        Assertions.assertEquals("joueur", InputUtils.pluralize("joueur", 0));
    }

    @Test
    @DisplayName("Pluralize long variant")
    void testPluralize_Long() {
        Assertions.assertEquals("blocs", InputUtils.pluralize("bloc", 100L));
        Assertions.assertEquals("bloc", InputUtils.pluralize("bloc", 1L));
    }
}
