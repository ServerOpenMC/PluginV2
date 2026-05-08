package fr.openmc.core.features.city.sub.mayor.perks;

import lombok.Getter;

@Getter
public enum PerkCategory {
	MILITARY("feature.city.mayor.perk.category.military"),
	STRATEGY("feature.city.mayor.perk.category.strategy"),
	AGRICULTURAL("feature.city.mayor.perk.category.agricultural"),
	ECONOMIC("feature.city.mayor.perk.category.economic"),
    DREAM("feature.city.mayor.perk.category.onirique"),
    ;

    private final String nameKey;

    PerkCategory(String nameKey) {
        this.nameKey = nameKey;
    }
}
