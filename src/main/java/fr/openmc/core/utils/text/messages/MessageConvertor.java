package fr.openmc.core.utils.text.messages;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageConvertor {
    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%(\\d+)\\$s|%s");

    public static String convert(String legacyPattern) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(legacyPattern);
        StringBuilder result = new StringBuilder();
        int autoIndex = 0;

        while (matcher.find()) {
            int index = matcher.group(1) != null
                    ? Integer.parseInt(matcher.group(1)) - 1
                    : autoIndex++;
            matcher.appendReplacement(result, "{" + index + "}");
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public static MessageFormat toMessageFormat(String legacyPattern, Locale locale) {
        return new MessageFormat(convert(legacyPattern), locale);
    }

    public static String toLegacy(String miniMessage) {
        return LEGACY.serialize(MINI.deserialize(miniMessage));
    }
}
