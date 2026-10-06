package fr.openmc.core.features.singularity.sub.wormhole.models;

import fr.openmc.core.features.singularity.contents.mobs.WormHole;
import lombok.Getter;

@Getter
public enum WormHoleStage {
    STAGE_1(1, 3.0f, 30, true),
    STAGE_2(2, 5.5f, 20, true),
    STAGE_3(3, 11f, 10, false)
    ;

    private final int stage;
    private final float scale;
    private final int flipDuration;
    private final boolean isThunderOn;

    WormHoleStage(int stage, float scale, int flipDuration, boolean isThunderOn) {
        this.stage = stage;
        this.scale = scale;
        this.flipDuration = flipDuration;
        this.isThunderOn = isThunderOn;
    }

    public static WormHoleStage getStage(int stage) {
        return WormHoleStage.values()[stage - 1];
    }

    public static WormHoleStage getNextStage(WormHoleStage initialStage) {
        if (initialStage.stage == 3)
            return null;
        return WormHoleStage.values()[initialStage.stage];
    }
}
