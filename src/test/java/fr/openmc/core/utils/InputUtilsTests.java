package fr.openmc.core.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.junit.jupiter.api.*;

public class InputUtilsTests {
    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("convertion Sign Input to Money")
    public void convertSignInputToMoney() {
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

        Assertions.assertEquals(
                -1,
                InputUtils.convertToMoneyValue("-3")
        );

        Assertions.assertEquals(
                -1,
                InputUtils.convertToMoneyValue("-1")
        );

        Assertions.assertEquals(
                -1,
                InputUtils.convertToMoneyValue("489y")
        );

        Assertions.assertEquals(
                -1,
                InputUtils.convertToMoneyValue("1.1")
        );

        Assertions.assertEquals(
                -1,
                InputUtils.convertToMoneyValue("4,5")
        );

    }

    @Test
    @DisplayName("check is returned value is true")
    public void isInputMoney() {
        Assertions.assertEquals(
                false,
                InputUtils.isInputMoney("0")
        );

        Assertions.assertEquals(
                true,
                InputUtils.isInputMoney("1")
        );

        Assertions.assertEquals(
                true,
                InputUtils.isInputMoney("3m")
        );
        Assertions.assertEquals(
                true,
                InputUtils.isInputMoney("3k")
        );

        Assertions.assertEquals(
                true,
                InputUtils.isInputMoney("3M")
        );
        Assertions.assertEquals(
                true,
                InputUtils.isInputMoney("3K")
        );

        Assertions.assertEquals(
                true,
                InputUtils.isInputMoney("1")
        );
        Assertions.assertEquals(
                true,
                InputUtils.isInputMoney("3000")
        );

        Assertions.assertEquals(
                false,
                InputUtils.isInputMoney("-3")
        );

        Assertions.assertEquals(
                false,
                InputUtils.isInputMoney("-1")
        );

        Assertions.assertEquals(
                false,
                InputUtils.isInputMoney("489y")
        );

        Assertions.assertEquals(
                false,
                InputUtils.isInputMoney("1.1")
        );

        Assertions.assertEquals(
                false,
                InputUtils.isInputMoney("4,5")
        );


    }
}
