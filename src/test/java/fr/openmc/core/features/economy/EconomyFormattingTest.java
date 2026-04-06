package fr.openmc.core.features.economy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

class EconomyFormattingTest {

    @BeforeAll
    static void setUpLocale() {
        Locale.setDefault(Locale.US);
    }

    @Test
    @DisplayName("Format zero balance")
    void testFormat_Zero() {
        Assertions.assertEquals("0", EconomyManager.getFormattedSimplifiedNumber(0));
    }

    @Test
    @DisplayName("Format small number without suffix")
    void testFormat_SmallNumber() {
        Assertions.assertEquals("500", EconomyManager.getFormattedSimplifiedNumber(500));
    }

    @Test
    @DisplayName("Format thousands with k suffix")
    void testFormat_Thousands() {
        String result = EconomyManager.getFormattedSimplifiedNumber(1500);
        Assertions.assertEquals("1.5k", result);
    }

    @Test
    @DisplayName("Format exact thousand")
    void testFormat_ExactThousand() {
        Assertions.assertEquals("1k", EconomyManager.getFormattedSimplifiedNumber(1000));
    }

    @Test
    @DisplayName("Format millions with M suffix")
    void testFormat_Millions() {
        Assertions.assertEquals("3M", EconomyManager.getFormattedSimplifiedNumber(3_000_000));
    }

    @Test
    @DisplayName("Format billions with B suffix")
    void testFormat_Billions() {
        Assertions.assertEquals("1B", EconomyManager.getFormattedSimplifiedNumber(1_000_000_000));
    }

    @Test
    @DisplayName("Format with decimal truncation")
    void testFormat_Decimal() {
        String result = EconomyManager.getFormattedSimplifiedNumber(2_500_000);
        Assertions.assertEquals("2.5M", result);
    }

    @Test
    @DisplayName("Format number under 1000")
    void testFormat_Under1000() {
        Assertions.assertEquals("999", EconomyManager.getFormattedSimplifiedNumber(999));
    }
}
