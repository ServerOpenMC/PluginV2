package fr.openmc.core.features.dream.registries.loottable;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.loottable.CustomLoot;
import fr.openmc.core.registry.loottable.CustomLootTable;

import java.util.Set;

public class MetalDetectorLootTable extends CustomLootTable {
    @Override
    public String getNamespace() {return "omc_dream:metal_detector";}

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new CustomLoot(
                        DreamItemRegistry.CHIPS_DIHYDROGENE,
                        0.4,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.CHIPS_JIMMY,
                        0.2,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.CHIPS_TERRE,
                        0.4,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.CHIPS_SANS_PLOMB,
                        0.4,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.CHIPS_NATURE,
                        0.4,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.CHIPS_AYWEN,
                        0.1,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.CHIPS_LAIT_2_MARGOUTA,
                        0.005,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.SOMNIFERE,
                        0.4,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.MUD_ORB,
                        0.05,
                        1,
                        1
                ),
                new CustomLoot(
                        OMCRegistry.CUSTOM_ENCHANTS.EXPERIENTASTIC.getEnchantedBookItem(1),
                        0.03,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.CRYSTALIZED_PICKAXE,
                        0.1,
                        1,
                        1
                )
        );
    }
}
