package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement;

import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.reward.EmptyReward;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.reward.PointReward;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.reward.plush.HalloweenRoot2026;
import lombok.Getter;

@Getter
public enum PointRewards {

    NULL(new EmptyReward(),-1, null),
    HALLOWEEN_ROOT_PLUSH(new HalloweenRoot2026(), 1000000, PointRewards.NULL),
    ;

    private final PointReward reward;
    private final int globalPointsRequire;
    private final PointRewards nextReward;

    PointRewards(PointReward reward, int globalPointsRequire, PointRewards nextReward) {
        this.reward = reward;
        this.globalPointsRequire = globalPointsRequire;
        this.nextReward = nextReward;
    }
}
