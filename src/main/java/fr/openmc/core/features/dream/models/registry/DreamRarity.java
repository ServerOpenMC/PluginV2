package fr.openmc.core.features.dream.models.registry;

import lombok.Getter;

@Getter
public enum DreamRarity {
    COMMON("§f§lITEM COMMUN"),
    RARE("§9§lITEM RARE"),
    EPIC("§5§lITEM EPIQUE"),
    LEGENDARY("§6§lITEM LEGENDAIRE"),
    ONIRISIME("§b§lITEM ONIRISME");

    private final String templateLore;

    DreamRarity(String templateLore) {
        this.templateLore = templateLore;
    }
}
