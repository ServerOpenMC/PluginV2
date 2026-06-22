package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.fishing;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.registry.SeaCreatureLoot;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;

import java.util.Set;

public class SeaCreatureLootTable extends CustomLootTable {
    @Override
    public String getNamespace() {
        return "omc_daily_events:sea_creature";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.SEA_GUARD, 0.4),
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.ANCIENT_VILLAGER, 0.1),
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.CHICKEN_JOCKEY, 0.6),
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.POISSON_STEVE, 0.3),
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.ANGRY_WITCH, 0.2),
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.LEVIATHAN, 0.1)
        );
    }
}