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

public class AncientMobLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.bloody_night.loot_table.ancient_mob");
    }

    @Override
    public String getNamespace() {
        return "omc_daily_events:ancient_mobs";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(Material.IRON_BLOCK,0.3, 3, 6),
                new ItemLoot(Material.GOLD_BLOCK,0.2, 1, 2),
                new ItemLoot(Material.DIAMOND_BLOCK,0.13, 1),
                new ItemLoot(Material.NETHERITE_SCRAP,0.1, 2),
                new ItemLoot(OMCRegistry.CUSTOM_ENCHANTS.VAMPIRISM.getEnchantedBookItem(1),0.02, 1)
        ));
    }
}
