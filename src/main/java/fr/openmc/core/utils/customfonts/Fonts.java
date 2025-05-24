package fr.openmc.core.utils.customfonts;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;

public class Fonts {
    public static String getFont(String namespaceID){
        return "§r" + new FontImageWrapper(namespaceID).getString();
    }
}
