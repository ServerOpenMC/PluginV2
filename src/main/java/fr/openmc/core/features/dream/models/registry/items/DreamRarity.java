package fr.openmc.core.features.dream.models.registry.items;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Getter
public enum DreamRarity {
    COMMON(Component.text("ITEM COMMUN", NamedTextColor.WHITE, TextDecoration.BOLD), "§f"),
    RARE(Component.text("ITEM RARE", NamedTextColor.BLUE, TextDecoration.BOLD), "§9"),
    EPIC(Component.text("ITEM EPIQUE", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD), "§5"),
    LEGENDARY(Component.text("ITEM LEGENDAIRE", NamedTextColor.GOLD, TextDecoration.BOLD), "§6"),
    ONIRISIME(Component.text("ITEM ONIRISME", NamedTextColor.AQUA, TextDecoration.BOLD), "§b");

    private final Component templateLore;
    private final String legacyColor;

    DreamRarity(Component templateLore, String legacyColor) {
        this.templateLore = templateLore;
        this.legacyColor = legacyColor;
    }
}
