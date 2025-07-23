package fr.openmc.core.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ColorUtils {

    private ColorUtils() {
        throw new IllegalStateException("For Sonar");
    }

    private static final Map<NamedTextColor, NamedTextColor> colorToReadable = new HashMap<>();
    static {
        colorToReadable.put(BLACK, DARK_GRAY);
        colorToReadable.put(DARK_BLUE, DARK_BLUE);
        colorToReadable.put(DARK_GREEN, DARK_GREEN);
        colorToReadable.put(DARK_AQUA, DARK_AQUA);
        colorToReadable.put(DARK_RED, DARK_RED);
        colorToReadable.put(DARK_PURPLE, DARK_PURPLE);
        colorToReadable.put(GOLD, GOLD);
        colorToReadable.put(GRAY, GRAY);
        colorToReadable.put(DARK_GRAY, DARK_GRAY);
        colorToReadable.put(BLUE, BLUE);
        colorToReadable.put(GREEN, GREEN);
        colorToReadable.put(AQUA, AQUA);
        colorToReadable.put(RED, RED);
        colorToReadable.put(LIGHT_PURPLE, LIGHT_PURPLE);
        colorToReadable.put(YELLOW, GOLD);
        colorToReadable.put(WHITE, GRAY);
    }

    /**
     * Retourne une couleur plus visible sur les Livres (blanc sur blanc ça se voit pas)
     */
    public static NamedTextColor getReadableColor(NamedTextColor c) {
        return colorToReadable.getOrDefault(c, null);
    }

    private static final Map<NamedTextColor, Material> colorToMaterial = new HashMap<>();
    static {
        colorToMaterial.put(BLACK, Material.BLACK_WOOL);
        colorToMaterial.put(DARK_BLUE, Material.BLUE_WOOL);
        colorToMaterial.put(DARK_GREEN, Material.GREEN_WOOL);
        colorToMaterial.put(DARK_AQUA, Material.CYAN_WOOL);
        colorToMaterial.put(DARK_RED, Material.RED_WOOL);
        colorToMaterial.put(DARK_PURPLE, Material.PURPLE_WOOL);
        colorToMaterial.put(GOLD, Material.ORANGE_WOOL);
        colorToMaterial.put(GRAY, Material.LIGHT_GRAY_WOOL);
        colorToMaterial.put(DARK_GRAY, Material.GRAY_WOOL);
        colorToMaterial.put(BLUE, Material.LIGHT_BLUE_WOOL);
        colorToMaterial.put(GREEN, Material.LIME_WOOL);
        colorToMaterial.put(AQUA, Material.CYAN_WOOL);
        colorToMaterial.put(RED, Material.RED_WOOL);
        colorToMaterial.put(LIGHT_PURPLE, Material.MAGENTA_WOOL);
        colorToMaterial.put(YELLOW, Material.YELLOW_WOOL);
        colorToMaterial.put(WHITE, Material.WHITE_WOOL);
    }

    /**
     * Retourne une laine de couleur en fonction de la couleur rentré
     */
    public static Material getMaterialFromColor(NamedTextColor c) {
        return colorToMaterial.getOrDefault(c, null);
    }

    /**
     * Retourne une couleur en fonction du String (LIGHT_PURPLE => NamedTextColor.LIGHT_PURPLE)
     */
    public static NamedTextColor getNamedTextColor(String color) {
        if (color == null) {
            return NamedTextColor.WHITE;
        }
        return NamedTextColor.NAMES.valueOr(color.toLowerCase(), NamedTextColor.WHITE);
    }

    private static final Map<NamedTextColor, String> colorToName = new HashMap<>();
    static {
        colorToName.put(BLACK, "§0Noir");
        colorToName.put(DARK_BLUE, "§1Bleu Foncé");
        colorToName.put(DARK_GREEN, "§2Vert Foncé");
        colorToName.put(DARK_AQUA, "§3Aqua Foncé");
        colorToName.put(DARK_RED, "§4Rouge Foncé");
        colorToName.put(DARK_PURPLE, "§5Violet");
        colorToName.put(GOLD, "§6Orange");
        colorToName.put(GRAY, "§7Gris");
        colorToName.put(DARK_GRAY, "§8Gris Foncé");
        colorToName.put(BLUE, "§9Bleu");
        colorToName.put(GREEN, "§aVert Clair");
        colorToName.put(AQUA, "§bBleu Clair");
        colorToName.put(RED, "§cRouge");
        colorToName.put(LIGHT_PURPLE, "§dRose");
        colorToName.put(YELLOW, "§eJaune");
        colorToName.put(WHITE, "§fBlanc");
    }

    /**
     * Retourne un String qui contient la couleur rentré
     */
    public static String getNameFromColor(NamedTextColor c) {
        return colorToName.getOrDefault(c, "Aucun");
    }

    private static final Map<NamedTextColor, String> colorCode = new HashMap<>();
    static {
        colorCode.put(BLACK, "§0");
        colorCode.put(DARK_BLUE, "§1");
        colorCode.put(DARK_GREEN, "§2");
        colorCode.put(DARK_AQUA, "§3");
        colorCode.put(DARK_RED, "§4");
        colorCode.put(DARK_PURPLE, "§5");
        colorCode.put(GOLD, "§6");
        colorCode.put(GRAY, "§7");
        colorCode.put(DARK_GRAY, "§8");
        colorCode.put(BLUE, "§9");
        colorCode.put(GREEN, "§a");
        colorCode.put(AQUA, "§b");
        colorCode.put(RED, "§c");
        colorCode.put(LIGHT_PURPLE, "§d");
        colorCode.put(YELLOW, "§e");
        colorCode.put(WHITE, "§f");
    };

    /**
     * Retourne un code couleur en § en fonction de la couleur donnée
     */
    public static String getColorCode(NamedTextColor color) {
        return colorCode.getOrDefault(color, "§f");
    }

    private static final Map<NamedTextColor, int[]> COLOR_RGB_MAP = Map.ofEntries(
            Map.entry(BLACK, new int[]{0, 0, 0}),
            Map.entry(DARK_BLUE, new int[]{0, 0, 170}),
            Map.entry(DARK_GREEN, new int[]{0, 170, 0}),
            Map.entry(DARK_AQUA, new int[]{0, 170, 170}),
            Map.entry(DARK_RED, new int[]{170, 0, 0}),
            Map.entry(DARK_PURPLE, new int[]{170, 0, 170}),
            Map.entry(GOLD, new int[]{255, 170, 0}),
            Map.entry(GRAY, new int[]{170, 170, 170}),
            Map.entry(DARK_GRAY, new int[]{85, 85, 85}),
            Map.entry(BLUE, new int[]{85, 85, 255}),
            Map.entry(GREEN, new int[]{85, 255, 85}),
            Map.entry(AQUA, new int[]{85, 255, 255}),
            Map.entry(RED, new int[]{255, 85, 85}),
            Map.entry(LIGHT_PURPLE, new int[]{255, 85, 255}),
            Map.entry(YELLOW, new int[]{255, 255, 85}),
            Map.entry(WHITE, new int[]{255, 255, 255})
    );

    public static int[] getRGBFromNamedTextColor(NamedTextColor color) {
        return COLOR_RGB_MAP.getOrDefault(color, new int[]{255, 255, 255});
    }
}
