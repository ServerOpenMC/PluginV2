package fr.openmc.core.utils.messages;

import lombok.Getter;
import org.bukkit.Sound;

@Getter
public enum MessageType {
    ERROR("§c❗", Sound.BLOCK_ANVIL_LAND),
    WARNING("§6⚠", Sound.BLOCK_ANVIL_LAND),
    SUCCESS("§a✔", Sound.BLOCK_NOTE_BLOCK_PLING),
    INFO("§bℹ", Sound.BLOCK_NOTE_BLOCK_PLING),
    NONE("", null)
    ;

    private final String prefix;
    private final Sound sound;

    MessageType(String prefix, Sound sound) {
        this.prefix = prefix;
        this.sound = sound;
    }
}