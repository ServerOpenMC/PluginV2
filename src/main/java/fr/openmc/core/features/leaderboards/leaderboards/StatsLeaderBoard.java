package fr.openmc.core.features.leaderboards.leaderboards;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.core.features.leaderboards.LeaderBoard;
import fr.openmc.core.utils.cache.CacheStatistic;
import fr.openmc.core.utils.text.ColorUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class StatsLeaderBoard extends LeaderBoard {
    private final Statistic statistic;
    private final Component header;
    private final TriFunction<OMCOfflinePlayer, Integer, Component, Component> componentBuilder;

    public StatsLeaderBoard(Statistic statistic,
                            Component header,
                            TriFunction<OMCOfflinePlayer, Integer, Component, Component> componentBuilder) {
        super(statistic.getKey().asMinimalString());
        this.statistic = statistic;
        this.header = header;
        this.componentBuilder = componentBuilder;
    }

    @Override
    public double getUpdateDelay() {
        return 15;
    }

    @Override
    public Component createComponent() {
        List<Map.Entry<OfflinePlayer, Integer>> stats =
                Arrays.stream(Bukkit.getOfflinePlayers())
                        .filter(p -> p.getName() != null)
                        .map(p -> Map.entry(p, CacheStatistic.getStatistic(p, statistic)))
                        .sorted(Map.Entry.<OfflinePlayer, Integer>comparingByValue().reversed())
                        .limit(10)
                        .toList();

        if (stats.isEmpty())
            return TranslationManager.translation("feature.leaderboards.empty.players")
                    .color(NamedTextColor.RED);

        Component text = header
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD);

        for (int i = 0; i < stats.size(); i++) {
            Map.Entry<OfflinePlayer, Integer> stat = stats.get(i);

            Component rank = Component.text("#" + (i + 1))
                    .color(ColorUtils.getRankColor(i + 1));

            OMCOfflinePlayer player = OMCOfflinePlayer.of(stat.getKey());

            int value = stat.getValue();

            text = text.append(Component.text("\n")
                    .append(componentBuilder.apply(player, value, rank)));
        }

        return text.append(Component.text("\n")
                .append(TranslationManager.translation("feature.leaderboards.footer")
                        .color(NamedTextColor.DARK_PURPLE)
                        .decorate(TextDecoration.BOLD)));
    }
}

