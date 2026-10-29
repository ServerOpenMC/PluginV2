package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.floor;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.OMCRegistry;
import lombok.Getter;

import java.util.List;

@Getter
public enum Floors {
    
    FLOOR_1(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_1, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_ZOMBIE_LV1,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SPIDER_LV1,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SKELETON_LV1
    ), 3,
            List.of(
                    BlockVector3.at(-57, -59, -58),
                    BlockVector3.at(-40, -59, -63),
                    BlockVector3.at(-64, -59, -72)
            )
    ), 0),
    
    FLOOR_2(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_2, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_ZOMBIE_LV1,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SPIDER_LV1,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SKELETON_LV1
    ), 3, List.of()), 1000),
    
    FLOOR_3(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_3, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_ZOMBIE_LV1,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SPIDER_LV1,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SKELETON_LV1
    ), 3, List.of()), 5000),
    
    FLOOR_4(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_4, List.of(), 5, List.of()), 10000),

    FLOOR_5(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_5, List.of(), 5, List.of()), 15000),
    
    FLOOR_6(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_6, List.of(), 5, List.of()), 20000),
    
    FLOOR_7(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_7, List.of(), 5, List.of()), 25000),
    
    FLOOR_8(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_8, List.of(), 5, List.of()), 30000),
    
    FLOOR_9(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_9, List.of(), 5, List.of()), 35000),
    
    FLOOR_10(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_10, List.of(), 7, List.of()), 40000),
    
    FLOOR_11(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_11, List.of(), 7, List.of()), 45000),
    
    FLOOR_12(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_12, List.of(), 7, List.of()), 50000),
    
    FLOOR_13(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_13, List.of(), 7, List.of()), 59000),
    
    FLOOR_14(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_14, List.of(), 7, List.of()), 65000),
    
    FLOOR_15(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_15, List.of(), 3, List.of()), 100000);
    

    private final Floor floor;
    private final int require;

    Floors(Floor floor, int require) {
        this.floor = floor; // 15 au totale
        this.require = require; // 500k global au totale
    }

    public String getSimplifyFloor() {
        return "Floor " + (ordinal() + 1);
    }
}
