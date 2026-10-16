package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement;

import fr.openmc.core.OMCRegistry;
import lombok.Getter;

import java.util.List;

@Getter
public enum Floors {
    FLOOR_1(new Floor(
            OMCRegistry.CUSTOM_REGIONS.EXAMPLE_REGION,
            List.of(),
            3.0
    ), 0),

    FLOOR_2(new Floor(
            OMCRegistry.CUSTOM_REGIONS.EXAMPLE_REGION,
            List.of(),
            3.0
    ), 1000),

    ;

    private final Floor floor;
    private final int require;

    Floors(Floor floor, int require) {
        this.floor = floor;
        this.require = require;
    }
}
