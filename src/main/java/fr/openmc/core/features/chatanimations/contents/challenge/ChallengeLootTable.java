package fr.openmc.core.features.chatanimations.contents.challenge;

import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.MoneyLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;

import java.util.Set;

public class ChallengeLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.chatanimations.challenge.table.name");
    }

    @Override
    public String getNamespace() {
        return "omc:challenge_table";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new MoneyLoot(200, 450, 1)
        );
    }
}
