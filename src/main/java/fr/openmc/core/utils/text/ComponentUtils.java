package fr.openmc.core.utils.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ComponentUtils {
    /**
     * Transforme les ComponentLike, en commponent
     * par défaut blanc et pas en italic si le component n'a pas de décoration par défaut
     * @param args Les Components
     * @return Les Components transformés
     */
    public static ComponentLike[] normalizeComponent(ComponentLike... args) {
        if (args == null || args.length == 0) return new ComponentLike[0];

        ComponentLike[] normalized = new ComponentLike[args.length];
        for (int i = 0; i < args.length; i++) {
            ComponentLike like = args[i];
            if (like == null) {
                normalized[i] = Component.empty();
                continue;
            }
            Component component = like.asComponent();
            normalized[i] = component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).colorIfAbsent(NamedTextColor.WHITE);
        }
        return normalized;
    }
}
