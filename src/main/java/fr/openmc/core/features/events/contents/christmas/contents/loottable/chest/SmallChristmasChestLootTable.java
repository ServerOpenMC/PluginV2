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

public class SmallChristmasChestLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.events.christmas.loot_table.small_chest");
    }

    @Override
    public String getNamespace() {
        return "omc_christmas:small_chest";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new EmptyLoot(0.5),
                new ItemLoot(Material.SNOWBALL,0.6, 3, 6),
                new ItemLoot(Material.CHEST,0.5, 1),
                new ItemLoot(Material.PAPER,0.3, 1, 2),
                new ItemLoot(Material.PAPER,0.2, 5)
        ));
    }
}
