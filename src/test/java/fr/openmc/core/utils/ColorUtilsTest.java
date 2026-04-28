package fr.openmc.core.utils;

import fr.openmc.core.utils.text.ColorUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ColorUtilsTest {

    @Test
    @DisplayName("getNamedTextColor valid color")
    void testGetNamedTextColor_Valid() {
        Assertions.assertEquals(NamedTextColor.RED, ColorUtils.getNamedTextColor("red"));
        Assertions.assertEquals(NamedTextColor.BLUE, ColorUtils.getNamedTextColor("blue"));
        Assertions.assertEquals(NamedTextColor.GREEN, ColorUtils.getNamedTextColor("green"));
    }

    @Test
    @DisplayName("getNamedTextColor null returns WHITE")
    void testGetNamedTextColor_Null() {
        Assertions.assertEquals(NamedTextColor.WHITE, ColorUtils.getNamedTextColor(null));
    }

    @Test
    @DisplayName("getNamedTextColor invalid returns WHITE")
    void testGetNamedTextColor_Invalid() {
        Assertions.assertEquals(NamedTextColor.WHITE, ColorUtils.getNamedTextColor("not_a_color"));
    }

    @Test
    @DisplayName("getColorCode returns correct codes")
    void testGetColorCode() {
        Assertions.assertEquals("§c", ColorUtils.getColorCode(NamedTextColor.RED));
        Assertions.assertEquals("§a", ColorUtils.getColorCode(NamedTextColor.GREEN));
        Assertions.assertEquals("§9", ColorUtils.getColorCode(NamedTextColor.BLUE));
        Assertions.assertEquals("§f", ColorUtils.getColorCode(NamedTextColor.WHITE));
        Assertions.assertEquals("§0", ColorUtils.getColorCode(NamedTextColor.BLACK));
    }

    @Test
    @DisplayName("getNameFromColor returns French names")
    void testGetNameFromColor() {
        Assertions.assertEquals("§cRouge", ColorUtils.getNameFromColor(NamedTextColor.RED));
        Assertions.assertEquals("§fBlanc", ColorUtils.getNameFromColor(NamedTextColor.WHITE));
        Assertions.assertEquals("§6Orange", ColorUtils.getNameFromColor(NamedTextColor.GOLD));
    }

    @Test
    @DisplayName("getMaterialFromColor returns correct wool")
    void testGetMaterialFromColor() {
        Assertions.assertEquals(Material.RED_WOOL, ColorUtils.getMaterialFromColor(NamedTextColor.RED));
        Assertions.assertEquals(Material.WHITE_WOOL, ColorUtils.getMaterialFromColor(NamedTextColor.WHITE));
        Assertions.assertEquals(Material.BLACK_WOOL, ColorUtils.getMaterialFromColor(NamedTextColor.BLACK));
    }

    @Test
    @DisplayName("getReadableColor maps correctly")
    void testGetReadableColor() {
        Assertions.assertEquals(NamedTextColor.DARK_GRAY, ColorUtils.getReadableColor(NamedTextColor.BLACK));
        Assertions.assertEquals(NamedTextColor.GRAY, ColorUtils.getReadableColor(NamedTextColor.WHITE));
        Assertions.assertEquals(NamedTextColor.GOLD, ColorUtils.getReadableColor(NamedTextColor.YELLOW));
        Assertions.assertEquals(NamedTextColor.RED, ColorUtils.getReadableColor(NamedTextColor.RED));
    }

    @Test
    @DisplayName("getRGBFromNamedTextColor returns correct RGB")
    void testGetRGBFromNamedTextColor() {
        int[] red = ColorUtils.getRGBFromNamedTextColor(NamedTextColor.RED);
        Assertions.assertArrayEquals(new int[]{255, 85, 85}, red);

        int[] black = ColorUtils.getRGBFromNamedTextColor(NamedTextColor.BLACK);
        Assertions.assertArrayEquals(new int[]{0, 0, 0}, black);

        int[] white = ColorUtils.getRGBFromNamedTextColor(NamedTextColor.WHITE);
        Assertions.assertArrayEquals(new int[]{255, 255, 255}, white);
    }
}
