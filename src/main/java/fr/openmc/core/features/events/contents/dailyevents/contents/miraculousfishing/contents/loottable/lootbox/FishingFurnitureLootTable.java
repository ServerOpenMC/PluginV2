package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.lootbox;

import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;

import java.util.Set;

public class FishingFurnitureLootTable extends CustomLootTable {
    @Override
    public String getNamespace() {
        return "omc_daily_events:fishing_furniture";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                // todo: put furniture in contents item adders
        );
    }
}