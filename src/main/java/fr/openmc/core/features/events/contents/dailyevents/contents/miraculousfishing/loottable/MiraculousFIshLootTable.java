package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.loottable;

import fr.openmc.core.registry.loottable.CustomLoot;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.LootMoney;

import java.util.Set;

public class MiraculousFIshLootTable extends CustomLootTable  {
    @Override
    public String getNamespace() {
        return "omc_daily_events:miraculous_fishing";
    }

    // todo stop here
    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new LootMoney(50, 250)
        );
    }
}
