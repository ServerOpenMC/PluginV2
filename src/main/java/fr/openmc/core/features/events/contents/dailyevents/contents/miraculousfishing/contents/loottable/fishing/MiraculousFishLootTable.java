package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.fishing;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.*;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MiraculousFishLootTable extends CustomLootTable  {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.miraculousfishing.loot_table.miraculous_fishing");
    }

    @Override
    public String getNamespace() {
        return "omc_daily_events:miraculous_fishing";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new TableLoot(OMCRegistry.CUSTOM_LOOT_TABLES.BASIC_FISHING, Material.COD, 0.7, false), // gerer par simulateLaunchLoot
                new MoneyLoot(50, 250, 0.4),
                new TableLoot(OMCRegistry.CUSTOM_LOOT_TABLES.SEA_CREATURE, Material.DROWNED_SPAWN_EGG, 0.25, false),
                new LootboxLoot(OMCRegistry.CUSTOM_LOOTBOXES.RARE_FISHING_TREASURE, 0.1),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.SPONGE_BOB, 0.07, 1, 1)

        ));
    }
}
