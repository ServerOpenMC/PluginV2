package fr.openmc.core.features.dream.models.registry.items;

import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum DreamRarity {
    COMMON(Component.text("§f§lITEM COMMUN")),
    RARE(Component.text("§9§lITEM RARE")),
    EPIC(Component.text("§5§lITEM EPIQUE")),
    LEGENDARY(Component.text("§6§lITEM LEGENDAIRE")),
    ONIRISIME(Component.text("§b§lITEM ONIRISME"));

    private final Component templateLore;

    DreamRarity(Component templateLore) {
        this.templateLore = templateLore;
    }
}
