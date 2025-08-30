package fr.openmc.core.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * TODO: Réactiver les tests quand MockBukkit fixera {@code  DIRT isn't an item}
 */
class ItemsUtilTest {

    @Disabled
    @Test
    @DisplayName("getItemTranslation with ItemStack")
    void testGetTranslationWithStack() {
        Assertions.assertEquals(
                "block.minecraft.chest",
                ItemUtils.getItemTranslation(new ItemStack(Material.CHEST)).key()
        );
    }

    @Disabled
    @Test
    @DisplayName("getItemTranslation with Material")
    void testGetTranslationWithMaterial() {
        Assertions.assertEquals(
                "block.minecraft.dirt",
                ItemUtils.getItemTranslation(Material.DIRT).key()
        );
    }

}
