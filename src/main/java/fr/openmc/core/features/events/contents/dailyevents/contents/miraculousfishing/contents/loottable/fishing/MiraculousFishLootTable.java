package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.fishing;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.LootboxLoot;
import fr.openmc.core.registry.loottable.loots.MoneyLoot;
import fr.openmc.core.registry.loottable.loots.TableLoot;

import java.util.Set;

public class MiraculousFishLootTable extends CustomLootTable  {
    @Override
    public String getNamespace() {
        return "omc_daily_events:miraculous_fishing";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new TableLoot(OMCRegistry.CUSTOM_LOOT_TABLES.BASIC_FISHING, 0.4),
                new MoneyLoot(50, 250, 0.3),
                // new ItemLoot(OMCRegistry.CUSTOM_ITEMS.BOB_SPONGE, 0.1, 1, 1),
                new LootboxLoot(OMCRegistry.CUSTOM_LOOTBOXES.FISHING_FURNITURE, 0.08),
                new LootboxLoot(OMCRegistry.CUSTOM_LOOTBOXES.RARE_FISHING_TREASURE, 0.1)
                // new TableLoot(OMCRegistry.CUSTOM_LOOT_TABLES.SEA_CREATURE, 0.2)
        );
    }
}
