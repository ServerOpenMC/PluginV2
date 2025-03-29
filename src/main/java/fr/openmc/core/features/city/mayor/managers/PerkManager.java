package fr.openmc.core.features.city.mayor.managers;

import fr.openmc.core.features.city.mayor.Perks;

public class PerkManager {
    public static Perks getPerkById(int id) {
        for (Perks perks : Perks.values()) {
            if (perks.getId() == id) return perks;
        }
        return null;
    }
}
