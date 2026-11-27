package fr.openmc.core.features.events.contents.christmas.contents.loottable.gift;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.contents.DreamItemRegistry;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.models.GiftType;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OnirismGiftLootTable extends CustomLootTable {
    @Override
    public @NonNull Component getName() {
        return TranslationManager.translation("feature.events.christmas.loot_table.onirism_gift");
    }

    @Override
    public String getNamespace() {
        return GiftType.ONIRISME.getId();
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.SANTA_CHESTPLATE, 0.25, 1),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.SANTA_LEGGINGS, 0.25, 1),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_LOOTBOX, 0.15, 1),
                new ItemLoot(DreamItemRegistry.EWENITE_BLOCK, 0.01, 1),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.PELUCHE_ROOT_HALLOWEEN_2026, 0.001, 1)
        ));
    }
}
