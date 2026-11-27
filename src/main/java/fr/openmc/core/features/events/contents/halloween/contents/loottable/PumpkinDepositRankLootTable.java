package fr.openmc.core.features.events.contents.halloween.contents.loottable;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.MoneyLoot;
import fr.openmc.core.registry.loottable.types.rank.RankLoot;
import fr.openmc.core.registry.loottable.types.rank.RankLootTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class PumpkinDepositRankLootTable extends RankLootTable {

    @Override
    public @NonNull Component getName() {
        return TranslationManager.translation("feature.events.halloween.loot_table.deposit_rank.name");
    }

    @Override
    public String getNamespace() {
        return "omc_halloween:pumpkin_deposit";
    }

    @Override
    protected List<RankLoot> getRanksByLoot() {
        return List.of(
                new RankLoot(1,
                        new ItemLoot((player) ->
                                OMCRegistry.DEPOSIT_EVENTS.HALLOWEEN.createTrophy(Material.PUMPKIN_PIE, 1, player),
                                Material.PUMPKIN_PIE,
                                1),
                        new MoneyLoot(30000),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 192)),
                new RankLoot(2,
                        new ItemLoot((player) ->
                                OMCRegistry.DEPOSIT_EVENTS.HALLOWEEN.createTrophy(Material.PUMPKIN_PIE, 2, player),
                                Material.PUMPKIN_PIE,
                                1),
                        new MoneyLoot(20000),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 128)),
                new RankLoot(3,
                        new ItemLoot((player) ->
                                OMCRegistry.DEPOSIT_EVENTS.HALLOWEEN.createTrophy(Material.PUMPKIN_PIE, 3, player),
                                Material.PUMPKIN_PIE,
                                1),
                        new MoneyLoot(10000),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 64)),
                new RankLoot(4, 10,
                        new MoneyLoot(6000)),
                new RankLoot(new MoneyLoot(3000))
        );
    }
}
