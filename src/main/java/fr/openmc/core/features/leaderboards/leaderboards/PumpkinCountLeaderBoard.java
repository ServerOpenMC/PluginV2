package fr.openmc.core.features.leaderboards.leaderboards;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.halloween.managers.HalloweenManager;
import fr.openmc.core.features.events.contents.halloween.models.HalloweenData;
import fr.openmc.core.features.leaderboards.LeaderBoard;
import fr.openmc.core.features.leaderboards.LeaderBoardManager;
import fr.openmc.core.utils.cache.CachePlayerName;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.entities.TextDisplay;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;

public class PumpkinCountLeaderBoard extends LeaderBoard {

    private static TextColor pumpkinColor = TextColor.color(156, 69, 26);

    public PumpkinCountLeaderBoard(){
        FileConfiguration config = YamlConfiguration.loadConfiguration(LeaderBoardManager.getLeaderBoardFile());
        float scale = (float) config.getDouble("scale");
        super("PumpkinCountLeaderBoard", config.getLocation("pumpkin-count-location"), null);
        if (this.location != null)
            this.display = new TextDisplay(createComponent(), this.location, new Vector3f(scale));
        else
            OMCLogger.warn("pumpkin-count-location is null");
    }

    @Override
    public void update() {
        if(this.display != null)
            this.display.updateText(createComponent());
    }

    private Component createComponent() {
        if (HalloweenManager.getAllHalloweenData().isEmpty())
            return TranslationManager.translation("feature.leaderboards.empty.players")
                    .color(NamedTextColor.RED);

        List<HalloweenData> datas = HalloweenManager.getAllHalloweenData().values().stream()
                .sorted(Comparator.comparingDouble(HalloweenData::getPumpkinCount))
                .limit(10)
                .toList();

        Component text = TranslationManager.translation("feature.leaderboards.header.pumpkin")
                .color(pumpkinColor)
                .decorate(TextDecoration.BOLD);

        for (int i = 0; i < datas.size(); i++){
            HalloweenData data = datas.get(i);
            String name = CachePlayerName.getName(data.getPlayerUUID());
            String formattedPumpkinCount = EconomyManager.getFormattedSimplifiedNumber(data.getPumpkinCount());
            Component rank = Component.text("#" + i).color(LeaderBoardManager.getRankColor(i));
            text = text.append(Component.text().append(TranslationManager.translation(
                    "feature.leaderboards.line.pumpkin",
                    rank,
                    Component.text(name).color(pumpkinColor),
                    Component.text(formattedPumpkinCount).color(NamedTextColor.WHITE))));

        }

        return text.append(Component.text("\n")
                .append(TranslationManager.translation("feature.leaderboards.footer")
                        .color(pumpkinColor)
                        .decorate(TextDecoration.BOLD)));
    }
}
