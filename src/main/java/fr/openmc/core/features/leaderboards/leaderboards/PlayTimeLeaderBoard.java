package fr.openmc.core.features.leaderboards.leaderboards;

import fr.openmc.core.features.leaderboards.LeaderBoard;
import fr.openmc.core.utils.cache.CachePlaytime;
import fr.openmc.core.utils.text.ColorUtils;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlayTimeLeaderBoard extends LeaderBoard {

    @Override
    public double getUpdateDelay() {
        return 15;
    }

    @Override
    public String getId() {
        return "playtime";
    }

    @Override
    public Component createComponent() {
        List<Map.Entry<OfflinePlayer, Long>> stats = Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(p -> p.getName() != null)
                .map(p -> Map.entry(p, CachePlaytime.getPlaytime(p)))
                .sorted(Map.Entry.<OfflinePlayer, Long>comparingByValue().reversed())
                .limit(10)
                .toList();

        if (stats.isEmpty())
            return TranslationManager.translation("feature.leaderboards.empty.players")
                    .color(NamedTextColor.RED);

        Component text = TranslationManager.translation("feature.leaderboards.header.playtime")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD);
        for (int i = 0; i < stats.size(); i++) {
            Map.Entry<OfflinePlayer, Long> stat = stats.get(i);
            Component rank = Component.text("#" + (i + 1)).color(ColorUtils.getRankColor(i + 1));

            String playerName = stat.getKey().getName();
            String time = DateUtils.convertTime(stat.getValue());
            if (playerName == null) continue;

            text = text.append(Component.text("\n")
                    .append(TranslationManager.translation(
                            "feature.leaderboards.line.playtime",
                            rank,
                            Component.text(playerName).color(NamedTextColor.LIGHT_PURPLE),
                            Component.text(time).color(NamedTextColor.WHITE)
                    )));
        }

        return text.append(Component.text("\n")
                .append(TranslationManager.translation("feature.leaderboards.footer")
                        .color(NamedTextColor.DARK_PURPLE)
                        .decorate(TextDecoration.BOLD)));
    }

}
