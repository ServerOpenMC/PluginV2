package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.floor;

import fr.openmc.core.OMCRegistry;
import lombok.Getter;

import java.util.List;

@Getter
public enum Floors {
    
    FLOOR_1(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 8), 0),
    
    FLOOR_2(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 8), 1000),
    
    FLOOR_3(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 8), 5000),
    
    FLOOR_4(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 10), 10000),
    
    FLOOR_5(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 10), 15000),
    
    FLOOR_6(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 10), 20000),
    
    FLOOR_7(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 10), 25000),
    
    FLOOR_8(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 10), 30000),
    
    FLOOR_9(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 10), 35000),
    
    FLOOR_10(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 10), 40000),
    
    FLOOR_11(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 10), 45000),
    
    FLOOR_12(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 10), 50000),
    
    FLOOR_13(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 10), 59000),
    
    FLOOR_14(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 10), 65000),
    
    FLOOR_15(new Floor(OMCRegistry.CUSTOM_REGIONS.TEST_REGION, List.of(), 3.0, 10), 100000);
    

    private final Floor floor;
    private final int require;

    Floors(Floor floor, int require) {
        this.floor = floor; // 15 au totale
        this.require = require; // 500k global au totale
    }
}
