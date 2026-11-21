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

public class MediumChristmasChestLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.events.christmas.loot_table.medium_chest");
    }

    @Override
    public String getNamespace() {
        return "omc_christmas:medium_chest";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new EmptyLoot(0.4),
                new ItemLoot(Material.CHEST,0.6, 1, 3),
                new ItemLoot(Material.PAPER,0.5, 5, 10),
                new ItemLoot(Material.BARREL,0.3, 1, 2),
                new ItemLoot(Material.ENDER_CHEST,0.01, 1),
                new ItemLoot(Material.PAPER,0.01, 40)
        ));
    }
}
