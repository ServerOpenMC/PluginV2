package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.loottable;

import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BloodyMobLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.bloody_night.loot_table.bloody_mob");
    }

    @Override
    public String getNamespace() {
        return "omc_daily_events:bloody_mobs";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(Material.IRON_INGOT,0.3, 1, 5),
                new ItemLoot(Material.GOLD_INGOT,0.2, 2, 4),
                new ItemLoot(Material.DIAMOND,0.1, 1, 2),
                new ItemLoot(Material.IRON_BLOCK,0.08, 1, 2),
                new ItemLoot(Material.GOLD_BLOCK,0.06, 1, 2),
                new ItemLoot(Material.DIAMOND_BLOCK,0.03, 1),
                new ItemLoot(Material.NETHERITE_INGOT,0.004, 1)
        ));
    }
}
