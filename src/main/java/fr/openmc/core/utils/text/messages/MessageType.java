package fr.openmc.core.utils.text.messages;

import fr.openmc.core.hooks.BedrockHook;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Getter
public enum MessageType {
    ERROR("§c❗", "§c!", Sound.BLOCK_NOTE_BLOCK_BASS),
    WARNING("§6⚠", "§6!", Sound.BLOCK_NOTE_BLOCK_HARP),
    SUCCESS("§a✔", "§aOK", Sound.BLOCK_NOTE_BLOCK_PLING),
    INFO("§bℹ", "§bi", Sound.BLOCK_NOTE_BLOCK_BIT),
    NONE("", "", null)
    ;

    private final String prefix;
    private final String bedrockPrefix;
    private final Sound sound;

    MessageType(String prefix, String bedrockPrefix, Sound sound) {
        this.prefix = prefix;
        this.bedrockPrefix = bedrockPrefix;
        this.sound = sound;
    }

    public String getPrefix(Player player) {
        return BedrockHook.isBedrockPlayer(player) ? bedrockPrefix : prefix;
    }
}