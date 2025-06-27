package fr.openmc.core.utils;

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
        colorToReadable.put(NamedTextColor.BLACK, NamedTextColor.DARK_GRAY);
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

    /**
     * Retourne une couleur plus visible sur les Livres (blanc sur blanc ça se voit pas)
     */
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
        colorToName.put(NamedTextColor.BLACK, "§0Noir");
        colorToName.put(NamedTextColor.DARK_BLUE, "§1Bleu Foncé");
        colorToName.put(NamedTextColor.DARK_GREEN, "§2Vert Foncé");
        colorToName.put(NamedTextColor.DARK_AQUA, "§3Aqua Foncé");
        colorToName.put(NamedTextColor.DARK_RED, "§4Rouge Foncé");
        colorToName.put(NamedTextColor.DARK_PURPLE, "§5Violet");
        colorToName.put(NamedTextColor.GOLD, "§6Orange");
        colorToName.put(NamedTextColor.GRAY, "§7Gris");
        colorToName.put(NamedTextColor.DARK_GRAY, "§8Gris Foncé");
        colorToName.put(NamedTextColor.BLUE, "§9Bleu");
        colorToName.put(NamedTextColor.GREEN, "§aVert Clair");
        colorToName.put(NamedTextColor.AQUA, "§bBleu Clair");
        colorToName.put(NamedTextColor.RED, "§cRouge");
        colorToName.put(NamedTextColor.LIGHT_PURPLE, "§dRose");
        colorToName.put(NamedTextColor.YELLOW, "§eJaune");
        colorToName.put(NamedTextColor.WHITE, "§fBlanc");
    }

    /**
     * Retourne un String qui contient la couleur rentré
     */
    public static String getNameFromColor(NamedTextColor c) {
        return colorToName.getOrDefault(c, "Aucun");
    }

    private static final Map<NamedTextColor, String> colorCode = new HashMap<>();
    static {
        colorCode.put(NamedTextColor.BLACK, "§0");
        colorCode.put(NamedTextColor.DARK_BLUE, "§1");
        colorCode.put(NamedTextColor.DARK_GREEN, "§2");
        colorCode.put(NamedTextColor.DARK_AQUA, "§3");
        colorCode.put(NamedTextColor.DARK_RED, "§4");
        colorCode.put(NamedTextColor.DARK_PURPLE, "§5");
        colorCode.put(NamedTextColor.GOLD, "§6");
        colorCode.put(NamedTextColor.GRAY, "§7");
        colorCode.put(NamedTextColor.DARK_GRAY, "§8");
        colorCode.put(NamedTextColor.BLUE, "§9");
        colorCode.put(NamedTextColor.GREEN, "§a");
        colorCode.put(NamedTextColor.AQUA, "§b");
        colorCode.put(NamedTextColor.RED, "§c");
        colorCode.put(NamedTextColor.LIGHT_PURPLE, "§d");
        colorCode.put(NamedTextColor.YELLOW, "§e");
        colorCode.put(NamedTextColor.WHITE, "§f");
    }

    /**
     * Retourne un code couleur en § en fonction de la couleur donnée
     */
    public static String getColorCode(NamedTextColor color) {
        return colorCode.getOrDefault(color, "§f");
    }

}
