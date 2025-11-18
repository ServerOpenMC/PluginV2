package fr.openmc.core.features.city.sub.mayor.perks;

import lombok.Getter;

@Getter
public enum PerkCategory {
	MILITARY("§8§oRéformes militaires"),
	STRATEGY("§8§oRéformes de stratégie"),
	AGRICULTURAL("§8§oRéformes d'agriculture"),
	ECONOMIC("§8§oRéformes économiques"),
    ;

    private final String name;

    PerkCategory(String name) {
        this.name = name;
    }
}
