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
    ), 3, List.of(
            BlockVector3.at(-32, -51, -74),
            BlockVector3.at(-48, -51, -67),
            BlockVector3.at(-60, -51, -68),
            BlockVector3.at(-58, -51, -49),
            BlockVector3.at(-35, -51, -52)
    )), 0),

    FLOOR_2(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_2, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_ZOMBIE_LV1,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SPIDER_LV1,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SKELETON_LV1
    ), 3, List.of(
            BlockVector3.at(-59, -43, -76),
            BlockVector3.at(-56, -43, -57)
    )), 1000),

    FLOOR_3(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_3, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_ZOMBIE_LV1,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SPIDER_LV1,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SKELETON_LV1
    ), 3, List.of(
            BlockVector3.at(-58, -35, -61),
            BlockVector3.at(-64, -35, -47)
    )), 5000),

    FLOOR_4(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_4, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_ZOMBIE_LV2,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SKELETON_LV2
    ), 5, List.of(
            BlockVector3.at(-76, -34, -65),
            BlockVector3.at(-48, -34, -51)
    )), 10000),

    FLOOR_5(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_5, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_ZOMBIE_LV2,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SKELETON_LV2
    ), 5, List.of(
            BlockVector3.at(-49, -27, -58),
            BlockVector3.at(-37, -27, -71),
            BlockVector3.at(-62, -27, -71)
    )), 15000),

    FLOOR_6(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_6, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_ZOMBIE_LV2,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SPIDER_LV2
    ), 5, List.of(
            BlockVector3.at(-59, -19, -72),
            BlockVector3.at(-64, -19, -59),
            BlockVector3.at(-45, -19, -63)
    )), 20000),

    FLOOR_7(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_7, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_ZOMBIE_LV2,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SKELETON_LV2,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SPIDER_LV2
    ), 5, List.of(
            BlockVector3.at(-54, -11, -51),
            BlockVector3.at(-63, -11, -69),
            BlockVector3.at(-46, -11, -61)
    )), 25000),

    FLOOR_8(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_8, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_ZOMBIE_LV1
    ), 5, List.of(
            BlockVector3.at(-48, -3, -56),
            BlockVector3.at(-60, -3, -62)
    )), 30000),

    FLOOR_9(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_9, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_BLAZE_LV2,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SKELETON_LV2
    ), 5, List.of(
            BlockVector3.at(-70, 5, -73),
            BlockVector3.at(-57, 5, -63),
            BlockVector3.at(-72, 5, -47),
            BlockVector3.at(-44, 5, -68),
            BlockVector3.at(-50, 5, -48)
    )), 35000),

    FLOOR_10(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_10, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_BLAZE_LV2,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SKELETON_LV2,
            OMCRegistry.CUSTOM_MOBS.HOUSE_WITHER_SKELETON_LV2
    ), 7, List.of(
            BlockVector3.at(-68, 14, -73),
            BlockVector3.at(-46, 14, -67),
            BlockVector3.at(-44, 14, -81),
            BlockVector3.at(-52, 14, -49),
            BlockVector3.at(-67, 14, -53)
    )), 40000),

    FLOOR_11(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_11, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_SPIDER_LV2,
            OMCRegistry.CUSTOM_MOBS.HOUSE_SKELETON_LV2
    ), 7, List.of(
            BlockVector3.at(-46, 21, -49),
            BlockVector3.at(-60, 21, -73),
            BlockVector3.at(-41, 21, -73)
    )), 45000),

    FLOOR_12(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_12, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_SPIDER_LV2,
            OMCRegistry.CUSTOM_MOBS.HOUSE_MOSSY_SKELETON_LV2
    ), 7, List.of(
            BlockVector3.at(-57, 29, -62),
            BlockVector3.at(-70, 29, -56),
            BlockVector3.at(-74, 29, -80)
    )), 50000),

    FLOOR_13(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_13, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_SPIDER_LV2,
            OMCRegistry.CUSTOM_MOBS.HOUSE_ZOMBIE_LV3
    ), 7, List.of(
            BlockVector3.at(-44, 37, -81),
            BlockVector3.at(-64, 37, -53),
            BlockVector3.at(-46, 37, -49),
            BlockVector3.at(-65, 37, -77)
    )), 59000),

    FLOOR_14(new Floor(OMCRegistry.CUSTOM_REGIONS.FLOOR_14, List.of(
            OMCRegistry.CUSTOM_MOBS.HOUSE_WITCH_LV3,
            OMCRegistry.CUSTOM_MOBS.HOUSE_BRUTE_LV3
    ), 7, List.of(
            BlockVector3.at(-63, 45, -76),
            BlockVector3.at(-68, 45, -63),
            BlockVector3.at(-51, 45, -70)
    )), 65000),

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
