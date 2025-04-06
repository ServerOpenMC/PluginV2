package fr.openmc.core.features.contest.managers;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class ColorUtils {

    private ColorUtils() {
        throw new IllegalStateException("For Sonar");
    }

    private static final Map<NamedTextColor, NamedTextColor> colorToReadable = new HashMap<>();
    static {
        colorToReadable.put(NamedTextColor.BLACK, NamedTextColor.BLACK);
        colorToReadable.put(NamedTextColor.DARK_BLUE, NamedTextColor.DARK_BLUE);
        colorToReadable.put(NamedTextColor.DARK_GREEN, NamedTextColor.DARK_GREEN);
        colorToReadable.put(NamedTextColor.DARK_AQUA, NamedTextColor.DARK_AQUA);
        colorToReadable.put(NamedTextColor.DARK_RED, NamedTextColor.DARK_RED);
        colorToReadable.put(NamedTextColor.DARK_PURPLE, NamedTextColor.DARK_PURPLE);
        colorToReadable.put(NamedTextColor.GOLD, NamedTextColor.GOLD);
        colorToReadable.put(NamedTextColor.GRAY, NamedTextColor.GRAY);
        colorToReadable.put(NamedTextColor.DARK_GRAY, NamedTextColor.DARK_GRAY);
        colorToReadable.put(NamedTextColor.BLUE, NamedTextColor.BLUE);
        colorToReadable.put(NamedTextColor.GREEN, NamedTextColor.GREEN);
        colorToReadable.put(NamedTextColor.AQUA, NamedTextColor.AQUA);
        colorToReadable.put(NamedTextColor.RED, NamedTextColor.RED);
        colorToReadable.put(NamedTextColor.LIGHT_PURPLE, NamedTextColor.LIGHT_PURPLE);
        colorToReadable.put(NamedTextColor.YELLOW, NamedTextColor.GOLD);
        colorToReadable.put(NamedTextColor.WHITE, NamedTextColor.GRAY);
    }

    public static NamedTextColor getReadableColor(NamedTextColor c) {
        return colorToReadable.getOrDefault(c, null);
    }

    private static final Map<NamedTextColor, Material> colorToMaterial = new HashMap<>();
    static {
        colorToMaterial.put(NamedTextColor.BLACK, Material.BLACK_WOOL);
        colorToMaterial.put(NamedTextColor.DARK_BLUE, Material.BLUE_WOOL);
        colorToMaterial.put(NamedTextColor.DARK_GREEN, Material.GREEN_WOOL);
        colorToMaterial.put(NamedTextColor.DARK_AQUA, Material.CYAN_WOOL);
        colorToMaterial.put(NamedTextColor.DARK_RED, Material.RED_WOOL);
        colorToMaterial.put(NamedTextColor.DARK_PURPLE, Material.PURPLE_WOOL);
        colorToMaterial.put(NamedTextColor.GOLD, Material.ORANGE_WOOL);
        colorToMaterial.put(NamedTextColor.GRAY, Material.LIGHT_GRAY_WOOL);
        colorToMaterial.put(NamedTextColor.DARK_GRAY, Material.GRAY_WOOL);
        colorToMaterial.put(NamedTextColor.BLUE, Material.LIGHT_BLUE_WOOL);
        colorToMaterial.put(NamedTextColor.GREEN, Material.LIME_WOOL);
        colorToMaterial.put(NamedTextColor.AQUA, Material.CYAN_WOOL);
        colorToMaterial.put(NamedTextColor.RED, Material.RED_WOOL);
        colorToMaterial.put(NamedTextColor.LIGHT_PURPLE, Material.MAGENTA_WOOL);
        colorToMaterial.put(NamedTextColor.YELLOW, Material.YELLOW_WOOL);
        colorToMaterial.put(NamedTextColor.WHITE, Material.WHITE_WOOL);
    }

    public static Material getMaterialFromColor(NamedTextColor c) {
        return colorToMaterial.getOrDefault(c, null);
    }

    public static NamedTextColor getNamedTextColor(String color) {
        return NamedTextColor.NAMES.valueOr(color.toLowerCase(), NamedTextColor.WHITE);
    }

}
