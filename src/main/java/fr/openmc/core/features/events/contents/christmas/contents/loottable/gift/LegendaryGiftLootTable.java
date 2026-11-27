package fr.openmc.core.features.events.contents.christmas.contents.loottable.gift;

import fr.openmc.core.OMCRegistry;
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

public class LegendaryGiftLootTable extends CustomLootTable {
    @Override
    public @NonNull Component getName() {
        return TranslationManager.translation("feature.events.christmas.loot_table.legendary_gift");
    }

    @Override
    public String getNamespace() {
        return GiftType.LEGENDARY.getId();
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GINGERBREAD, 0.29, 80, 100),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.SANTA_HAT, 0.25, 1),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.SANTA_BOOTS, 0.25, 1),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_IT_TIME_DISC, 0.15, 1),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_LOOTBOX, 0.15, 1),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.ONIRISIME_GIFT, 0.005, 1)
        ));
    }
}
