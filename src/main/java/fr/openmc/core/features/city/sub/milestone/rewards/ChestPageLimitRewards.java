package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum ChestPageLimitRewards implements CityRewards {

    LEVEL_1(0),
    LEVEL_2(1),
    LEVEL_3(2),
    LEVEL_4(3),
    LEVEL_5(4),
    LEVEL_6(5),
    LEVEL_7(6),
    LEVEL_8(8),
    LEVEL_9(9),
    LEVEL_10(10);


    private final Integer chestPageLimit;

    ChestPageLimitRewards(Integer chestPageLimit) {
        this.chestPageLimit = chestPageLimit;
    }

    public static int getChestPageLimit(int level) {
        ChestPageLimitRewards[] values = ChestPageLimitRewards.values();

        if (level < 1 || level > values.length) {
            throw new IllegalArgumentException("Niveau invalide: " + level);
        }

        ChestPageLimitRewards reward = values[level - 1];
        if (reward.chestPageLimit != null) {
            return reward.chestPageLimit;
        }

        for (int i = level - 2; i >= 0; i--) {
            if (values[i].chestPageLimit != null) {
                return values[i].chestPageLimit;
            }
        }

        return 0;
    }

    @Override
    public Component getName() {
        return Component.text("§a" + chestPageLimit + " pages de coffre §7maximum");
    }
}
