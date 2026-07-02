package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.fishing;

import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BasicFishLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.miraculousfishing.loot_table.basic_fishing");
    }

    @Override
    public String getNamespace() {
        return "omc_daily_events:basic_fishing";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(Material.COD,0.4, 2, 4),
                new ItemLoot(Material.SALMON, 0.25, 2, 4),
                new ItemLoot(Material.PUFFERFISH, 0.13, 2, 4),
                new ItemLoot(Material.TROPICAL_FISH, 0.08, 2, 4)
        ));
    }
}
