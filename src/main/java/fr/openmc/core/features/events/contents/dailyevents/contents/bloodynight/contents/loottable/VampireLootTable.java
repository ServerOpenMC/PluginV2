package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.loottable;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VampireLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.bloody_night.loot_table.vampire_boss");
    }

    @Override
    public String getNamespace() {
        return "omc_daily_events.vampire_boss";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE_BLOCK,1, 1, 5),
                new ItemLoot(ItemType.OMINOUS_BOTTLE.createItemStack(
                        o -> o.setAmplifier(6)),0.6, 1),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.VAMPIRE_HEAD,0.50, 1),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ENCHANTS.VAMPIRISM.getEnchantedBookItem(1, 2),
                        0.25,
                        1
                )
        ));
    }
}