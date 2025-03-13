package fr.openmc.core.features.adminshop.menu.category.colored;

import lombok.Getter;

@Getter
public enum BlockType {
    CONCRETE("Béton"),
    CONCRETE_POWDER("Béton poudreux"),
    GLASS("Verre"),
    GLASS_PANE("Vitre"),
    TERRACOTTA("Terre cuite"),
    WOOL("Laine"),
    LOG("bûche"),
    LEAVES("feuilles"),
    DYE("teinture"),
    ;

    private final String name;
    BlockType(String name) {
        this.name = name;
    }

}

