package fr.openmc.core.features.dream.registries.loottable;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MetalDetectorLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dream.loot_table.metal_detector");
    }

    @Override
    public String getNamespace() {return "omc_dream:metal_detector";}

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.CHIPS_DIHYDROGENE,
                        0.4,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.CHIPS_JIMMY,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.CHIPS_TERRE,
                        0.4,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.CHIPS_SANS_PLOMB,
                        0.4,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.CHIPS_NATURE,
                        0.4,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.CHIPS_AYWEN,
                        0.1,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.CHIPS_LAIT_2_MARGOUTA,
                        0.005,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.SOMNIFERE,
                        0.4,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.MUD_ORB,
                        0.05,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ENCHANTS.EXPERIENTASTIC.getEnchantedBookItem(1),
                        0.03,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.CRYSTALIZED_PICKAXE,
                        0.1,
                        1,
                        1
                )
        ));
    }
}
