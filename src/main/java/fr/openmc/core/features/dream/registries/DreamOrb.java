package fr.openmc.core.features.dream.registries;

import lombok.Getter;

import java.util.Arrays;

public enum DreamOrb {
    SCULK_PLAINS_ORB(1),
    SOUL_FOREST_ORB(2),
    CLOUD_CASTLE_ORB(3),
    MUD_BEACH_ORB(4),
    GLACITE_GROTTO_ORB(5)
    ;

    @Getter
    private final int stepInt;

    DreamOrb(int stepInt) {
        this.stepInt = stepInt;
    }

    public static String[] getValues() {
        return Arrays.stream(DreamOrb.values())
                .map(Enum::name)
                .toArray(String[]::new);
    }
}
