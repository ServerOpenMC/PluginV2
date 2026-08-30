package fr.openmc.core.utils.text.fonts;

import fr.openmc.core.hooks.BedrockHook;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SmallCapsUtils {
    public final static Key SMALL_CAPS_FONT = Key.key("omc_fonts", "small_caps");

    public static Component toSmall(Player player, String text){
        Component component = Component.text(text.toLowerCase());

        return BedrockHook.isBedrockPlayer(player) ? component : component.font(SMALL_CAPS_FONT);
    }

    public static Component toSmallComponent(Player player, Component text) {
        return BedrockHook.isBedrockPlayer(player) ? toSmallComponentBedrock(text) : toSmallComponent(text);
    }

    public static Component toSmallComponent(Component text){
        return text.font(SMALL_CAPS_FONT);
    }

    public static Component toSmallComponentBedrock(Component component) {
        Component transformed = component;

        if (component instanceof TextComponent textComponent) {
            String converted = SmallCapsUtils.toSmallCapsChar(textComponent.content());
            transformed = textComponent.content(converted);
        }

        List<Component> newChildren = new ArrayList<>();
        for (Component child : component.children()) {
            newChildren.add(toSmallComponentBedrock(child));
        }
        return transformed.children(newChildren);
    }

    public static String toSmallCapsChar(String input) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            String charStr = input.substring(i, i + 1);

            String namespacedId = SMALL_CAPS_FONT.namespace() + ":"
                    + SMALL_CAPS_FONT.value() + ":" + charStr;

            Character bedrockChar = BedrockHook.getGlyph(namespacedId);
            if (bedrockChar != null) {
                sb.append(bedrockChar);
            } else {
                sb.append(charStr);
            }
            i++;
        }
        return sb.toString();
    }

}
