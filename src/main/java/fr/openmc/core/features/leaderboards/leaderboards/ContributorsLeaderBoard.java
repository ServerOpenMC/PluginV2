package fr.openmc.core.features.leaderboards.leaderboards;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.leaderboards.LeaderBoard;
import fr.openmc.core.features.leaderboards.LeaderBoardManager;
import fr.openmc.core.hooks.github.GitHubHook;
import fr.openmc.core.hooks.github.models.ContributorStats;
import fr.openmc.core.utils.text.ColorUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.entities.TextDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.*;

public class ContributorsLeaderBoard extends LeaderBoard {

    public ContributorsLeaderBoard(){
        FileConfiguration config = YamlConfiguration.loadConfiguration(LeaderBoardManager.getLeaderBoardFile());
        float scale = (float) config.getDouble("scale");
        super("contributors", config.getLocation("contributors-location"), null, 30*60); // 30 minutes
        if (this.location != null)
            this.display = new TextDisplay(createComponent(), this.location, new Vector3f(scale));
        else
            OMCLogger.warn("contributors-location is null");
    }

    @Override
    public Component createComponent() {
        GitHubHook.fetchContributorStats();
        List<Map.Entry<String, ContributorStats>> stats = new ArrayList<>();
        GitHubHook.getContributors().values().forEach(login -> stats.add(new AbstractMap.SimpleEntry<>(login, GitHubHook.getStats(login))));
        stats.sort((stat1, stat2) -> Integer.compare(stat2.getValue().getBrutLines(), stat1.getValue().getBrutLines()));
        if (stats.isEmpty())
            return TranslationManager.translation("feature.leaderboards.empty.contributors")
                    .color(NamedTextColor.RED);

        Component text = TranslationManager.translation("feature.leaderboards.header.contributors")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD);
        for (int i = 0;i < Math.min(10, stats.size()); i++){
            Map.Entry<String, ContributorStats> stat = stats.get(i);
            Component rank = Component.text("#" + (i+1)).color(ColorUtils.getRankColor(i+1));

            text = text.append(Component.text("\n")
                    .append(TranslationManager.translation(
                            "feature.leaderboards.line.contributors",
                            rank,
                            Component.text(stat.getKey()).color(NamedTextColor.LIGHT_PURPLE), // Name
                            Component.text(stat.getValue().totalAddLines()).color(NamedTextColor.WHITE),
                            Component.text(stat.getValue().totalRemoveLines()).color(NamedTextColor.WHITE)
                    )));
        }
        return text.append(Component.text("\n")
                .append(TranslationManager.translation("feature.leaderboards.footer")
                        .color(NamedTextColor.DARK_PURPLE)
                        .decorate(TextDecoration.BOLD)));
    }

    @Override
    public void setLocation(Location location) throws IOException {
        FileConfiguration config = YamlConfiguration.loadConfiguration(LeaderBoardManager.getLeaderBoardFile());
        config.set("contributors-location", location);
        config.save(LeaderBoardManager.getLeaderBoardFile());
        this.display.setLocation(location);
    }
}
