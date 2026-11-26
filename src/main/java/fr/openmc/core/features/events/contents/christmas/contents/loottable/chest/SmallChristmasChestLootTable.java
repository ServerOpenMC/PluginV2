package fr.openmc.core.features.events.contents.christmas.contents.loottable.chest;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.EmptyLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemType;

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
                new EmptyLoot(0.8),
                new ItemLoot(Material.SNOWBALL,0.4, 3, 6),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.COMMON_GIFT,0.3, 1),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GINGERBREAD,0.3, 1, 2),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GINGERBREAD,0.2, 5),
                new ItemLoot(ItemType.OMINOUS_BOTTLE.createItemStack(e -> e
                        .setAmplifier(1)), 0.1, 1),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.RARE_GIFT,0.01, 1)
        ));
    }
}
