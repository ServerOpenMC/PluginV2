package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.loottable;

import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.MoneyLoot;

import java.util.Set;

public class MiraculousFishLootTable extends CustomLootTable  {
    @Override
    public String getNamespace() {
        return "omc_daily_events:miraculous_fishing";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new MoneyLoot(50, 250)
        );
    }
}
