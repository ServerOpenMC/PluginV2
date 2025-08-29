package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum RankLimitRewards implements CityRewards {

    LEVEL_1(0),
    LEVEL_2(0),
    LEVEL_3(2),
    LEVEL_4(3),
    LEVEL_5(5),
    LEVEL_6(8),
    LEVEL_7(10),
    LEVEL_8(13),
    LEVEL_9(15),
    LEVEL_10(18);

    private final Integer rankLimit;

    RankLimitRewards(Integer rankLimit) {
        this.rankLimit = rankLimit;
    }

    public static int getRankLimit(int level) {
        RankLimitRewards[] values = RankLimitRewards.values();

        if (level < 1 || level > values.length) {
            throw new IllegalArgumentException("Niveau invalide: " + level);
        }

        RankLimitRewards reward = values[level - 1];
        if (reward.rankLimit != null) {
            return reward.rankLimit;
        }

        for (int i = level - 2; i >= 0; i--) {
            if (values[i].rankLimit != null) {
                return values[i].rankLimit;
            }
        }
        return 0;
    }

    @Override
    public Component getName() {
        return Component.text("§a" + rankLimit + " §7grades maximum");
    }
}
