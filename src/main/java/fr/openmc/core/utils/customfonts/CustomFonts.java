package fr.openmc.core.utils.customfonts;

import org.bukkit.Bukkit;

public abstract class CustomFonts {

    private static final boolean hasItemsAdder = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");

    /**
     * use exemple : CustomFonts.getBest("omc_homes:bin", "🗑️")
     *
     * @param namespaceID the namespaceID of the font
     * @param baseFont the base font
     * @return Best Font to use for the server
     */
    public static String getBest(String namespaceID, String baseFont) {
        String font = null;
        if (hasItemsAdder) font = Fonts.getFont(namespaceID);

        if (font == null) {
            font = baseFont;
        }

        return font;
    }
}