package fr.openmc.core.features.leaderboards.leaderboards;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.deposit.DepositEvent;
import fr.openmc.core.features.events.contents.deposit.DepositEventManager;
import fr.openmc.core.features.events.contents.deposit.models.DepositDataModel;
import fr.openmc.core.features.leaderboards.LeaderBoard;
import fr.openmc.core.utils.text.ColorUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

public class DepositLeaderBoard extends LeaderBoard {

    private final DepositEvent event;

    public DepositLeaderBoard(DepositEvent event) {
        super(event.getId() + "-deposit");
        this.event = event;
    }

    @Override
    public double getUpdateDelay() {
        return 15;
    }

    @Override
    public Component createComponent() {
        TextColor color = TextColor.color(event.getMainColor());

        List<DepositDataModel> top = DepositEventManager.getTop(event, event.getLeaderboardSize());
        if (top.isEmpty())
            return TranslationManager.translation("feature.leaderboards.empty.players")
                    .color(NamedTextColor.RED);

        Component text = TranslationManager.translation("feature.events.leaderboard.header", event.getName())
                .color(color)
                .decorate(TextDecoration.BOLD);

        for (int i = 0; i < top.size(); i++) {
            DepositDataModel data = top.get(i);
            OMCOfflinePlayer player = OMCOfflinePlayer.of(data.getPlayerUUID());
            Component rank = Component.text("#" + (i + 1)).color(ColorUtils.getRankColor(i + 1));
            String count = EconomyManager.getFormattedSimplifiedNumber(data.getCount());

            text = text.append(Component.text("\n").append(TranslationManager.translation(
                    event.key("leaderboard.line"),
                    rank,
                    player.getNameWithHead().color(color),
                    Component.text(count)
                            .color(NamedTextColor.WHITE)
            )));

        }

        return text.append(Component.text("\n")
                .append(TranslationManager.translation("feature.leaderboards.footer")
                        .color(color)
                        .decorate(TextDecoration.BOLD)));
    }
}
