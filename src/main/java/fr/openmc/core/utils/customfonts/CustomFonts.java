package fr.openmc.core.utils.customfonts;

import fr.openmc.api.hooks.ItemsAdderHook;

public abstract class CustomFonts {
    /**
     * use exemple : CustomFonts.getBest("omc_homes:bin", "🗑️")
     *
     * @param namespaceID the namespaceID of the font
     * @param baseFont the base font
     * @return Best Font to use for the server
     */
    public static String getBest(String namespaceID, String baseFont) {
        String font = null;
        if (ItemsAdderHook.hasItemAdder()) font = Fonts.getFont(namespaceID);

        if (font == null) {
            font = baseFont;
        }

        return font;
    }
}