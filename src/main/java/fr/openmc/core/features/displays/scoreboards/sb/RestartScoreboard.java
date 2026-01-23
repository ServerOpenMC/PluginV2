package fr.openmc.core.features.displays.scoreboards.sb;

import fr.openmc.api.scoreboard.SternalBoard;
import fr.openmc.core.commands.utils.Restart;
import fr.openmc.core.features.displays.scoreboards.BaseScoreboard;
import fr.openmc.core.utils.DateUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static fr.openmc.core.utils.messages.MessagesManager.textToSmall;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

public class RestartScoreboard extends BaseScoreboard {
    @Override
    protected void updateTitle(Player player, SternalBoard board) {
        board.updateTitle(getTitle());
    }

    @Override
    public void update(Player player, SternalBoard board) {
        List<Component> lines = new ArrayList<>();

        lines.add(empty());
        lines.add(text("%s %s".formatted(textToSmall("Redémarrage dans"), DateUtils.convertSecondToTime(Restart.remainingTime)), NamedTextColor.RED));
        lines.add(empty());
        lines.add(getFooter());

        board.updateLines(lines);
    }

    @Override
    public boolean shouldDisplay(Player player) {
        return Restart.isRestarting;
    }

    @Override
    public int priority() {
        return 999;
    }

    @Override
    protected int updateInterval() {
        return 1;
    }
}
