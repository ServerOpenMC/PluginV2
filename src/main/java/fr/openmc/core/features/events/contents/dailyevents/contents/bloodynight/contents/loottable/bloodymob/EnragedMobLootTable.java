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

public class EnragedMobLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.bloody_night.loot_table.enraged_mob");
    }

    @Override
    public String getNamespace() {
        return "omc_daily_events:enraged_mobs";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE,0.4, 4, 8),
                new ItemLoot(Material.IRON_BLOCK,0.23, 1),
                new ItemLoot(Material.GOLDEN_APPLE,0.2, 1, 2),
                new ItemLoot(Material.GOLD_BLOCK,0.16, 1),
                new ItemLoot(Material.DIAMOND,0.1, 1, 2),
                new ItemLoot(Material.DIAMOND_BLOCK,0.01, 1),
                new ItemLoot(Material.NETHERITE_SCRAP,0.004, 1)
        ));
    }
}
