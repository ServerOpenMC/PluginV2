package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum MascotsLevelsRewards implements CityRewards {

    LEVEL_1(1),
    LEVEL_2(2),
    LEVEL_3(3),
    LEVEL_4(4),
    LEVEL_5(5),
    LEVEL_6(6),
    LEVEL_7(7),
    LEVEL_8(8),
    LEVEL_9(9),
    LEVEL_10(10);


    private final Integer mascotsLevelLimit;

    MascotsLevelsRewards(Integer mascotsLevelLimit) {
        this.mascotsLevelLimit = mascotsLevelLimit;
    }

    public static int getMascotsLevelLimit(int level) {
        return level;
    }

    @Override
    public Component getName() {
        return Component.text("§cNiveau " + mascotsLevelLimit + " §7maximum pour la Mascotte");
    }
}
