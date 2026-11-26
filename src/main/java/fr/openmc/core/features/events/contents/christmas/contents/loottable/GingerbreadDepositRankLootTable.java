package fr.openmc.core.features.events.contents.christmas.contents.loottable;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.MoneyLoot;
import fr.openmc.core.registry.loottable.types.rank.RankLoot;
import fr.openmc.core.registry.loottable.types.rank.RankLootTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;

import java.util.List;

public class GingerbreadDepositRankLootTable extends RankLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.events.christmas.loot_table.deposit_rank.name");
    }

    @Override
    public String getNamespace() {
        return "omc_christmas:gingerbread_deposit";
    }

    // todo: a faire
    @Override
    protected List<RankLoot> getRanksByLoot() {
        return List.of(
                new RankLoot(1,
                        new MoneyLoot(30000, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 1, 192),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.PLUSH_GINGERBREAD_ROOT, 1, 1)),
                new RankLoot(2,
                        new MoneyLoot(20000, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 1, 128),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.PLUSH_GINGERBREAD_ROOT, 1, 1)),
                new RankLoot(3,
                        new MoneyLoot(10000, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 1, 64),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.PLUSH_GINGERBREAD_ROOT, 1, 1)),
                new RankLoot(4, 10,
                        new MoneyLoot(6000, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.PLUSH_GINGERBREAD_ROOT, 1, 1)),
                new RankLoot(
                        new MoneyLoot(3000, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.PLUSH_GINGERBREAD_ROOT, 1, 1))
        );
    }
}