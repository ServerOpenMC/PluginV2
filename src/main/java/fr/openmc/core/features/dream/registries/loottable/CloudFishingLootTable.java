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

public class CloudFishingLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dream.loot_table.cloud_fishing");
    }

    @Override
    public String getNamespace() { return "omc_dream:cloud_fishing"; }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.METEO_WAND,
                        0.05,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.POISSONION,
                        0.5,
                        1,
                        2
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.MOON_FISH,
                        0.5,
                        1,
                        2
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.SUN_FISH,
                        0.5,
                        1,
                        2
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.DOCKER_FISH,
                        0.1,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.DREAM_ITEM.SOMNIFERE,
                        0.4,
                        1,
                        1
                )
        ));
    }
}
