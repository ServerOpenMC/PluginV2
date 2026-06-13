package fr.openmc.core.utils.text.messages;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageConvertor {
    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    public static String toLegacy(String miniMessage) {
        return LEGACY.serialize(MINI.deserialize(miniMessage));
    }
}
