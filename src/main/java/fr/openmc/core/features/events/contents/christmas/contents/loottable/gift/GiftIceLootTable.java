package fr.openmc.core.features.events.contents.christmas.contents.loottable.gift;

import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.IdLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GiftIceLootTable extends CustomLootTable {
    @Override
    public @NonNull Component getName() {
        return TranslationManager.translation("feature.events.christmas.loot_table.gift_in_ice");
    }

    @Override
    public String getNamespace() {
        return "omc_christmas:gift_ice";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new IdLoot("common", 0.80),
                new IdLoot("rare", 0.10),
                new IdLoot("legendary", 0.05),
                new IdLoot("onirisme", 0.003)
        ));
    }
}
