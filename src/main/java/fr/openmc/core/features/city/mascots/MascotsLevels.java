package fr.openmc.core.features.city.mascots;

import lombok.Getter;

@Getter
public enum MascotsLevels {
    level1(300),
    level2(600),
    level3(900),
    level4(1200),
    level5(1500),
    level6(1800),
    level7(2100),
    level8(2400),
    level9(2700),
    level10(3000),;

    private final int Health;

    MascotsLevels(int health) {
        Health = health;
    }
}
