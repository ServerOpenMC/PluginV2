package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.loottable.bloodymob;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CorruptedMobLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.bloody_night.loot_table.corrupted_mob");
    }

    @Override
    public String getNamespace() {
        return "omc_daily_events:corrupted_mobs";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(Material.IRON_INGOT,0.4, 1, 4),
                new ItemLoot(Material.GOLD_INGOT,0.2, 1, 3),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE,0.2, 1, 3),
                new ItemLoot(Material.DIAMOND,0.07, 1, 2),
                new ItemLoot(Material.GOLDEN_APPLE,0.06, 1, 3)
        ));
    }
}
