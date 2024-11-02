package fr.openmc.core.utils.messages;

import lombok.Getter;

/**
 * Enum representing various prefixes for messages.
 * Each prefix is associated with a formatted string using custom colors and fonts.
 */
public enum Prefix {

    // Font: https://lingojam.com/MinecraftSmallFont
    // For gradient color: https://www.birdflop.com/resources/rgb/
    // Color format: §x§r§r§g§g§b§b§l

    OPENMC("§x§F§F§4§5§7§3§l@§x§F§F§4§D§7§9§lᴏ§x§F§F§5§5§7§F§lᴘ§x§F§F§5§D§8§5§lᴇ§x§F§F§6§4§8§B§lɴ§x§F§F§6§C§9§1§lᴍ§x§F§F§7§4§9§7§lᴄ"),
    STAFF("§x§8§0§1§4§1§4S§x§F§F§0§0§2§3t§x§F§F§0§0§2§3a§x§F§F§0§0§2§3f§x§F§F§0§0§2§3f"),
    CITY("§x§4§A§A§E§0§0ᴠ§x§4§3§B§5§0§Cɪ§x§3§B§B§B§1§9ʟ§x§3§4§C§2§2§5ʟ§x§2§C§C§8§3§1ᴇ")
    ;

    @Getter private final String prefix;
    Prefix(String prefix) {
        this.prefix = prefix;
    }
}