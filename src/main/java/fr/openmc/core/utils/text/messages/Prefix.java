package fr.openmc.core.utils.text.messages;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Enum representing various prefixes for messages.
 * Each prefix is associated with a formatted string using custom colors and fonts.
 */
public enum Prefix {

    // Font: https://mcutils.com/small-text-converter
    // For gradient color: https://www.birdflop.com/resources/rgb/
    // Color format: MiniMessage

    OPENMC("<gradient:#BD45E6:#F99BEB>OPENMC</gradient>"),
    STAFF("<gradient:#FF2929:#FF7676>STAFF</gradient>"),
    CITY("<gradient:#026404:#2E8F38>CITY</gradient>"),
    CONTEST("<gradient:#FFB800:#F0DF49>CONTEST</gradient>"),
    CORPSE("<gradient:#F82C5D:#F64545>CORPSE</gradient>"),
    HOME("<gradient:#80EF80:#9aec9a>HOME</gradient>"),
    FRIEND("<gradient:#68E98B:#0EFF6D>FRIEND</gradient>"),
    MAYOR("<gradient:#FCD05C:#FBEF22>MAYOR</gradient><#FBEF22>OR</#FBEF22>"),
    QUEST("<gradient:#4E76E3:#1A51E7>QUEST</gradient>"),
    BANK("<gradient:#084CFB:#ADB6FD>BANK</gradient>"),
    SHOP("<gradient:#084CFB:#5AAFC4>SHOP</gradient>"),
    ADMINSHOP("<gradient:#EE2222:#F04949>ADMINSHOP</gradient>"),
    DEATH("<gradient:#FF0000:#FF7F7F>☠</gradient>"),
    SETTINGS("<gradient:#2C82E0:#67C8FF>SETTINGS</gradient>"),
    MILLESTONE("<gradient:#A2D182:#B8E89D>MILESTONES</gradient>"),
    DREAM("<gradient:#4498DB:#412AEF>DREAM</gradient>"),
    MAILBOX("<gradient:#2C43C4:#6A76D9>MAILBOX</gradient>"),
    HALLOWEEN("<gradient:#FF7518:#FFD580>HALLOWEEN</gradient>"),
    DIMOPENER("<gradient:#A5FFA8:#DAFFE4>DIMOPENER</gradient>"),
    GOLDEN_HARVEST("<gradient:#EFDA3A:#93AE0E>GOLDEN HARVEST</gradient>"),
    MIRACULOUS_FISHING("<gradient:#4498DB:#0FB590>MIRACULOUS FISHING</gradient>");

    @Getter
    private final Component prefix;

    Prefix(String prefix) {
        this.prefix = MiniMessage.miniMessage().deserialize(prefix);
    }
}