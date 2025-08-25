package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum MemberLimitRewards implements CityRewards {

    LEVEL_1(2),
    LEVEL_2(3),
    LEVEL_3(5),
    LEVEL_4(null),
    LEVEL_5(7),
    LEVEL_6(10),
    LEVEL_7(15),
    LEVEL_8(20),
    LEVEL_9(null),
    LEVEL_10(25);


    private final Integer memberLimit;

    MemberLimitRewards(Integer memberLimit) {
        this.memberLimit = memberLimit;
    }

    public static int getMemberLimit(int level) {
        MemberLimitRewards[] values = MemberLimitRewards.values();

        if (level < 1 || level > values.length) {
            throw new IllegalArgumentException("Niveau invalide: " + level);
        }

        MemberLimitRewards reward = values[level - 1];
        if (reward.memberLimit != null) {
            return reward.memberLimit;
        }

        for (int i = level - 2; i >= 0; i--) {
            if (values[i].memberLimit != null) {
                return values[i].memberLimit;
            }
        }

        return 0;
    }

    @Override
    public Component getName() {
        return Component.text("§d" + memberLimit + " §7membres maximum");
    }
}
