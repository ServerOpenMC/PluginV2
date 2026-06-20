package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.lootbox;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import org.bukkit.Material;

import java.util.Set;

public class RareFishingTreasureLootTable extends CustomLootTable {
    @Override
    public String getNamespace() {
        return "omc_daily_events:rare_fishing_treasure";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new ItemLoot(Material.COD, Material.COD, 0.3, 32, 64),
                new ItemLoot(Material.SALMON, Material.SALMON, 0.3, 32, 64),
                new ItemLoot(Material.TROPICAL_FISH, Material.TROPICAL_FISH, 0.3, 32, 64),
                new ItemLoot(Material.PUFFERFISH, Material.PUFFERFISH, 0.3, 15, 32),
                new ItemLoot(Material.NAUTILUS_SHELL, Material.NAUTILUS_SHELL, 0.15, 2, 4),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.EPIC_FISHING_TREASURE, 0.1, 1, 1)

        );
    }
}