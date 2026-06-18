package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.fishing;

import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import org.bukkit.Material;

import java.util.Set;

public class BasicFishLootTable extends CustomLootTable {
    @Override
    public String getNamespace() {
        return "omc_daily_events:basic_fishing";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new ItemLoot(Material.COD, Material.COD, 0.6, 2, 4),
                new ItemLoot(Material.SALMON, Material.SALMON, 0.25, 2, 4),
                new ItemLoot(Material.TROPICAL_FISH, Material.TROPICAL_FISH, 0.02, 2, 4),
                new ItemLoot(Material.PUFFERFISH, Material.PUFFERFISH, 0.13, 2, 4)
        );
    }
}
