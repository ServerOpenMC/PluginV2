package fr.openmc.core.utils.messages;

import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.Arrays;

/**
 * Enum representing various prefixes for messages.
 * Each prefix is associated with a formatted string using custom colors and fonts.
 */
public enum Prefix {

    // Font: https://lingojam.com/MinecraftSmallFont
    // For gradient color: https://www.birdflop.com/resources/rgb/
    // Color format: MiniMessage

    OPENMC("<gradient:#BD45E6:#F99BEB>ᴏᴘᴇɴᴍᴄ</gradient>"),
    STAFF("<gradient:#AC3535:#8C052B>ѕᴛᴀꜰꜰ</gradient>"),
    CITY("<gradient:#026404:#2E8F38>ᴄɪᴛʏ</gradient>"),
    CONTEST("<gradient:#FFB800:#F0DF49>ᴄᴏɴᴛᴇѕᴛ</gradient>"),
    ENTREPRISE("<gradient:#E2244F:#FE7474>ᴇɴᴛʀᴇᴘʀɪѕᴇ</gradient>") // a changer si ça ne correspond pas
    ;

    @Getter private final String prefix;
    Prefix(String prefix) {
        this.prefix = prefix;
    }
}