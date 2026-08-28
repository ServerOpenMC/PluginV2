package fr.openmc.core.utils.text.messages;

import fr.openmc.core.utils.text.fonts.SmallCapsUtils;
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

    OPENMC("<gradient:#BD45E6:#F99BEB>openmc</gradient>"),
    STAFF("<gradient:#FF2929:#FF7676>staff</gradient>"),
    CITY("<gradient:#026404:#2E8F38>city</gradient>"),
    CONTEST("<gradient:#FFB800:#F0DF49>contest</gradient>"),
    CORPSE("<gradient:#F82C5D:#F64545>corpse</gradient>"),
    HOME("<gradient:#80EF80:#9aec9a>home</gradient>"),
    FRIEND("<gradient:#68E98B:#0EFF6D>friend</gradient>"),
    MAYOR("<gradient:#FCD05C:#FBEF22>mayor</gradient><#FBEF22>OR</#FBEF22>"),
    QUEST("<gradient:#4E76E3:#1A51E7>quest</gradient>"),
    BANK("<gradient:#084CFB:#ADB6FD>bank</gradient>"),
    SHOP("<gradient:#084CFB:#5AAFC4>shop</gradient>"),
    ADMINSHOP("<gradient:#EE2222:#F04949>adminshop</gradient>"),
    DEATH("<gradient:#FF0000:#FF7F7F>☠</gradient>", false),
    SETTINGS("<gradient:#2C82E0:#67C8FF>settings</gradient>"),
    MILLESTONE("<gradient:#A2D182:#B8E89D>milestones</gradient>"),
    DREAM("<gradient:#4498DB:#412AEF>dream</gradient>"),
    MAILBOX("<gradient:#2C43C4:#6A76D9>mailbox</gradient>"),
    HALLOWEEN("<gradient:#FF7518:#FFD580>halloween</gradient>"),
    DIMOPENER("<gradient:#A5FFA8:#DAFFE4>dimopener</gradient>"),
    GOLDEN_HARVEST("<gradient:#EFDA3A:#93AE0E>golden harvest</gradient>"),
    MIRACULOUS_FISHING("<gradient:#4498DB:#0FB590>miraculous fishing</gradient>");

    @Getter
    private final Component prefix;
    @Getter
    private final boolean toSmall;

    Prefix(String prefix) {
        this.prefix = MiniMessage.miniMessage().deserialize(prefix);
        this.toSmall = true;
    }

    Prefix(String prefix, boolean toSmall) {
        this.prefix = MiniMessage.miniMessage().deserialize(prefix);
        this.toSmall = toSmall;
    }

    public Component getPrefixComponent() {
        if (!toSmall) return prefix;
        return prefix.font(SmallCapsUtils.SMALL_CAPS_FONT);
    }
}