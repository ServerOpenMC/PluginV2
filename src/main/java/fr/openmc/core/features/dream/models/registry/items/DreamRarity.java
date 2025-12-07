package fr.openmc.core.features.dream.models.registry.items;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Getter
public enum DreamRarity {
    COMMON(Component.text("ITEM COMMUN", NamedTextColor.WHITE, TextDecoration.BOLD)),
    RARE(Component.text("ITEM RARE", NamedTextColor.BLUE, TextDecoration.BOLD)),
    EPIC(Component.text("ITEM EPIQUE", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)),
    LEGENDARY(Component.text("ITEM LEGENDAIRE", NamedTextColor.GOLD, TextDecoration.BOLD)),
    ONIRISIME(Component.text("ITEM ONIRISME", NamedTextColor.AQUA, TextDecoration.BOLD));

    private final Component templateLore;

    DreamRarity(Component templateLore) {
        this.templateLore = templateLore;
    }
}
