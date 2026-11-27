package fr.openmc.core.features.events.contents.christmas.contents.loottable.gift;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.models.GiftType;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.MoneyLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CommonGiftLootTable extends CustomLootTable {
    @Override
    public @NonNull Component getName() {
        return TranslationManager.translation("feature.events.christmas.loot_table.common_gift");
    }

    @Override
    public String getNamespace() {
        return GiftType.COMMON.getId();
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new MoneyLoot(50, 100, 0.35),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GINGERBREAD, 0.30, 5, 10),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 0.25, 5, 10),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.RARE_GIFT, 0.01, 1)
        ));
    }
}
