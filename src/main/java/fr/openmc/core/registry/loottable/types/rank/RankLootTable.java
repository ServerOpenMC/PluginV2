package fr.openmc.core.registry.loottable.types.rank;

import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.LootReward;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public abstract class RankLootTable extends CustomLootTable {
    protected abstract List<RankLoot> getRanksByLoot();

    public @Nullable CustomLootTable getReward(int rank) {
        return getSortedRanks().stream()
                .filter(r -> r.startRank() <= rank && r.endRank() >= rank)
                .findFirst()
                .map(RankLoot::loots)
                .orElse(null);
    }

    public List<RankLoot> getSortedRanks() {
        return getRanksByLoot().stream()
                .sorted(Comparator.comparingInt(
                        (RankLoot r) -> r.startRank() < 0 ? Integer.MAX_VALUE : r.startRank()))
                .toList();
    }

    @Override
    public void openMenu(Player player) {
        new RankLootsMenu(player, this.getName(), this.getSortedRanks()).open();
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of();
    }

    @Override
    public double getChanceOf(ItemStack item) {
        throw new Error("getChanceOf non supporté dans RankLootTable");
    }

    @Override
    public LootReward rollLoots(OfflinePlayer receiver) {
        throw new Error("rollLoots non supporté dans RankLootTable");
    }

    @Override
    public LootReward rollLoots() {
        throw new Error("rollLoots non supporté dans RankLootTable");
    }

    @Override
    public LootReward rollLootsWithAmount(Player receiver, int amountRoll) {
        throw new Error("rollLootsWithAmount non supporté dans RankLootTable");
    }

    @Override
    public LootReward rollLootsWithoutGuarantee(Player receiver) {
        throw new Error("rollLootsWithoutGuarantee non supporté dans RankLootTable");
    }

    @Override
    public CustomLoot selectRandomLoot() {
        throw new Error("selectRandomLoot non supporté dans RankLootTable");
    }

    @Override
    public LootReward generateWeightedPool() {
        throw new Error("generateWeightedPool non supporté dans RankLootTable");
    }

    @Override
    public LootReward generateChestLoot(Block block, Player receiver) {
        throw new Error("generateChestLoot non supporté dans RankLootTable");
    }
}
