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

public class RareGiftLootTable extends CustomLootTable {
    @Override
    public @NonNull Component getName() {
        return TranslationManager.translation("feature.events.christmas.loot_table.rare_gift");
    }

    @Override
    public String getNamespace() {
        return GiftType.RARE.getId();
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new MoneyLoot(300, 500, 0.35),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.GINGERBREAD, 0.30, 20, 40),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 0.25, 32, 55),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_LOOTBOX, 0.05, 1),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.LEGENDARY_GIFT, 0.01, 1)
        ));
    }
}
