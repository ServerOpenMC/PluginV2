package fr.openmc.core.registry.loottable.types.rank;

import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public record RankLoot(int startRank, int endRank, CustomLootTable loots) {
    public RankLoot(int rank, CustomLootTable loots) {
        this(rank, rank, loots);
    }

    public RankLoot(CustomLootTable loots) {
        this(-1, -1, loots);
    }

    public RankLoot(int startRank, int endRank, CustomLoot... loots) {
        this(startRank, endRank, getPreBuild(loots));
    }

    public RankLoot(int rank, CustomLoot... loots) {
        this(rank, rank, getPreBuild(loots));
    }

    public RankLoot(CustomLoot... loots) {
        this(-1, -1, getPreBuild(loots));
    }

    private static CustomLootTable getPreBuild(CustomLoot... loots) {
        return new CustomLootTable() {
            @Override
            public @NonNull Component getName() {
                return TranslationManager.translation("feature.events.christmas.loot_table.rank.name");
            }

            @Override
            public String getNamespace() {
                return "";
            }

            @Override
            public Set<CustomLoot> getLoots() {
                return Arrays.stream(loots)
                        .collect(Collectors.toSet());
            }
        };
    }
}
