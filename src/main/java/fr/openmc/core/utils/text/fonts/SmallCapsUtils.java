package fr.openmc.core.utils.text.fonts;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

public class SmallCapsUtils {
    private final static Key SMALL_CAPS_FONT = Key.key("omc_fonts", "small_caps");

    public static Component toSmallCaps(String text){
        return Component.text(text.toLowerCase()).font(SMALL_CAPS_FONT);
    }

    public static Component toSmallCaps(Component text){
        return text.font(SMALL_CAPS_FONT);
    }
}
