package fr.openmc.core.features.events.contents.christmas.contents.loottable;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.MoneyLoot;
import fr.openmc.core.registry.loottable.types.rank.RankLoot;
import fr.openmc.core.registry.loottable.types.rank.RankLootTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class GingerbreadDepositRankLootTable extends RankLootTable {
    @Override
    public @NonNull Component getName() {
        return TranslationManager.translation("feature.events.christmas.loot_table.deposit_rank.name");
    }

    @Override
    public String getNamespace() {
        return "omc_christmas:gingerbread_deposit";
    }

    @Override
    protected List<RankLoot> getRanksByLoot() {
        return List.of(
                new RankLoot(1,
                        new ItemLoot((player) ->
                                OMCRegistry.DEPOSIT_EVENTS.CHRISTMAS.createTrophy(OMCRegistry.CUSTOM_ITEMS.GINGERBREAD, 1, player),
                                OMCRegistry.CUSTOM_ITEMS.GINGERBREAD,
                                1),
                        new MoneyLoot(40000),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE_BLOCK, 32),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.PLUSH_GINGERBREAD_ROOT,1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.ONIRISIME_GIFT, 2),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_LOOTBOX, 3)),
                new RankLoot(2,
                        new ItemLoot((player) ->
                                OMCRegistry.DEPOSIT_EVENTS.CHRISTMAS.createTrophy(OMCRegistry.CUSTOM_ITEMS.GINGERBREAD, 1, player),
                                OMCRegistry.CUSTOM_ITEMS.GINGERBREAD,
                                2),
                        new MoneyLoot(30000),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE_BLOCK, 16),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.PLUSH_GINGERBREAD_ROOT,1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.ONIRISIME_GIFT, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_LOOTBOX, 2)),
                new RankLoot(3,
                        new ItemLoot((player) ->
                                OMCRegistry.DEPOSIT_EVENTS.CHRISTMAS.createTrophy(OMCRegistry.CUSTOM_ITEMS.GINGERBREAD, 1, player),
                                OMCRegistry.CUSTOM_ITEMS.GINGERBREAD,
                                4),
                        new MoneyLoot(15000),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE_BLOCK, 8),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.PLUSH_GINGERBREAD_ROOT, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_LOOTBOX, 1)),
                new RankLoot(4, 10,
                        new MoneyLoot(8000, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE_BLOCK, 4),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.PLUSH_GINGERBREAD_ROOT, 1)),
                new RankLoot(
                        new MoneyLoot(5000, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE_BLOCK, 2),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.PLUSH_GINGERBREAD_ROOT, 1))
        );
    }
}