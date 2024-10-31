package fr.openmc.core.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;

public class ItemsUtilTests {
    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("getItemTranslation with ItemStack")
    public void getTranslationWithStack() {
        Assertions.assertEquals(
                "block.minecraft.chest",
                ItemUtils.getItemTranslation(new ItemStack(Material.CHEST)).key()
        );
    }

    @Test
    @DisplayName("getItemTranslation with Material")
    public void getTranslationWithMaterial() {
        Assertions.assertEquals(
                "block.minecraft.dirt",
                ItemUtils.getItemTranslation(Material.DIRT).key()
        );
    }
}
