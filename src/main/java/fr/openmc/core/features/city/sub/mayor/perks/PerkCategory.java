package fr.openmc.core.features.city.sub.mayor.perks;

import lombok.Getter;

@Getter
public enum PerkCategory {
    MILITARY("§8§oRéformes Militaires"),
    STRATEGY("§8§oRéformes de Stratégie"),
    AGRICULTURAL("§8§oRéformes d'Agriculture"),
    ECONOMIC("§8§oRéformes Econnomiques"),
    ;

    private final String name;

    PerkCategory(String name) {
        this.name = name;
    }
}
