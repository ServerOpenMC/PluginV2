package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.chests.loottable;

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

public class LootChestLootTable extends CustomLootTable {

    @Override
    public Component getName() {
        return TranslationManager.translation("feature.events.halloween.festival.chest_lootbox");
    }

    @Override
    public String getNamespace() {
        return "omc_halloween:loot_chest_loot";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_YELLOW,0.003, 1, 2),// 0.0845% : 1/1884 coffre
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_RED,0.018, 1, 3),// 0.5068% : 1/198 coffre
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BLUE,0.055, 1, 3),// 1.5489% : 1/65 coffre
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_GREEN,0.075, 1, 3),// 2.1121% : 1/48 coffre
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN,0.4, 1, 5),// 11.2644% : 1/9 coffre
                new ItemLoot(Material.COBWEB,1, 2, 10),
                new ItemLoot(Material.SUGAR, 0.8, 2, 5),// 22.5289% : 1/5 coffre
                new ItemLoot(Material.REDSTONE, 0.5, 2, 5),// 14.0805% : 1/8 coffre
                new ItemLoot(Material.PUMPKIN, 0.7, 1)// 19.7128% : 1/6 coffre
        ));
    }
}
