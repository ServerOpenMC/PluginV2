package fr.openmc.core.utils.chronometer;

import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;

@Getter
public enum ChronometerType {
    ACTION_BAR(ChatMessageType.ACTION_BAR),
    CHAT(ChatMessageType.CHAT),
    SYSTEM(ChatMessageType.SYSTEM);

    private final ChatMessageType chatMessageType;

    ChronometerType(ChatMessageType chatMessageType) {
        this.chatMessageType = chatMessageType;
    }
}
