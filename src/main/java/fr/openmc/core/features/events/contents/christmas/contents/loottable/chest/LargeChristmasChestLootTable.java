package fr.openmc.core.features.events.contents.christmas.contents.loottable.chest;

import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.EmptyLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LargeChristmasChestLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.events.christmas.loot_table.large_chest");
    }

    @Override
    public String getNamespace() {
        return "omc_christmas:large_chest";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new EmptyLoot(0.2),
                new ItemLoot(Material.PAPER,0.8, 45, 64),
                new ItemLoot(Material.CHEST,0.7, 4, 6),
                new ItemLoot(Material.BARREL,0.6, 2, 5),
                new ItemLoot(Material.ENDER_CHEST,0.5, 1, 3),
                new ItemLoot(Material.BARREL,0.1, 8),
                new ItemLoot(Material.ENDER_CHEST,0.01, 5)
        ));
    }
}
