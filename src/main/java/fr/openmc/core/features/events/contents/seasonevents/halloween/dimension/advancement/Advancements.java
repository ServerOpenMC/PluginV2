package fr.openmc.core.features.events.contents.seasonevents.halloween.dimension.advancement;

import lombok.Getter;

@Getter
public enum Advancements {
    ROOM_1(1000)
    ;

    private final int require;

    Advancements(int require) {
        this.require = require;
    }
}
