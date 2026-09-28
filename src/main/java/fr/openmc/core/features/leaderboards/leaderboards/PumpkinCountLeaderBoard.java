package fr.openmc.core.features.leaderboards.leaderboards;

import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.halloween.managers.HalloweenManager;
import fr.openmc.core.features.events.contents.halloween.models.HalloweenData;
import fr.openmc.core.features.leaderboards.LeaderBoard;
import fr.openmc.core.utils.cache.CachePlayerName;
import fr.openmc.core.utils.text.ColorUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Comparator;
import java.util.List;

public class PumpkinCountLeaderBoard extends LeaderBoard {

    private final TextColor pumpkinColor = TextColor.color(156, 69, 26);

    @Override
    public double getUpdateDelay() {
        return 15;
    }

    @Override
    public String getId() {
        return "pumpkin-count";
    }

    @Override
    public Component createComponent() {
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
            Component name = CachePlayerName.name(data.getPlayerUUID());
            String formattedPumpkinCount = EconomyManager.getFormattedSimplifiedNumber(data.getPumpkinCount());
            Component rank = Component.text("#" + (i+1)).color(ColorUtils.getRankColor(i+1));
            text = text.append(Component.text("\n").append(TranslationManager.translation(
                    "feature.leaderboards.line.pumpkin",
                    rank,
                    name.color(pumpkinColor),
                    Component.text(formattedPumpkinCount).color(NamedTextColor.WHITE))));

        }

        return text.append(Component.text("\n")
                .append(TranslationManager.translation("feature.leaderboards.footer")
                        .color(pumpkinColor)
                        .decorate(TextDecoration.BOLD)));
    }
}
