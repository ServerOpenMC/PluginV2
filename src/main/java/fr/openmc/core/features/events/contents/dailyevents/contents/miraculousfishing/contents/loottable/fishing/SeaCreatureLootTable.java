package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.fishing;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.registry.SeaCreatureLoot;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SeaCreatureLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.miraculousfishing.loot_table.sea_creature");
    }

    @Override
    public String getNamespace() {
        return "omc_daily_events:sea_creature";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.CHICKEN_JOCKEY, 0.6),
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.SEA_GUARD, 0.4),
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.POISSON_STEVE, OMCRegistry.CUSTOM_ITEMS.POISSON_STEVE_HEAD, 0.3),
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.ANGRY_WITCH, 0.2),
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.ANCIENT_VILLAGER, 0.1),
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.LEVIATHAN, OMCRegistry.CUSTOM_ITEMS.LEVIATHAN_HEAD, 0.1),
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.GIANT_ELDER_GUARDIAN, 0.05),
                new SeaCreatureLoot(OMCRegistry.CUSTOM_MOBS.KRAKEN, OMCRegistry.CUSTOM_ITEMS.KRAKEN_HEAD, 0.03)
        ));
    }
}