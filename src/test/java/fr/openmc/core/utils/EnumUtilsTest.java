package fr.openmc.core.utils;

import org.bukkit.Material;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnumUtilsTest {

    @Test
    @DisplayName("Match valid enum value")
    void testMatch_Valid() {
        Assertions.assertEquals(Material.STONE, EnumUtils.match("stone", Material.class));
    }

    @Test
    @DisplayName("Match valid enum value case insensitive")
    void testMatch_CaseInsensitive() {
        Assertions.assertEquals(Material.DIAMOND, EnumUtils.match("diamond", Material.class));
        Assertions.assertEquals(Material.DIAMOND, EnumUtils.match("DIAMOND", Material.class));
        Assertions.assertEquals(Material.DIAMOND, EnumUtils.match("Diamond", Material.class));
    }

    @Test
    @DisplayName("Match invalid key returns null")
    void testMatch_InvalidReturnsNull() {
        Assertions.assertNull(EnumUtils.match("not_a_material", Material.class));
    }

    @Test
    @DisplayName("Match invalid key returns default value")
    void testMatch_InvalidReturnsDefault() {
        Assertions.assertEquals(Material.AIR, EnumUtils.match("not_a_material", Material.class, Material.AIR));
    }

    @Test
    @DisplayName("Match null key returns default")
    void testMatch_NullKey() {
        Assertions.assertEquals(Material.STONE, EnumUtils.match(null, Material.class, Material.STONE));
    }

    @Test
    @DisplayName("Match null key returns null without default")
    void testMatch_NullKeyNoDefault() {
        Assertions.assertNull(EnumUtils.match(null, Material.class));
    }
}
