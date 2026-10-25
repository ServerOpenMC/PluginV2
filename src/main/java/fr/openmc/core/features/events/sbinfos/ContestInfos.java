package fr.openmc.core.features.events.sbinfos;

import fr.openmc.core.features.displays.scoreboards.sb.InfoSwapper;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.Contest;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.ContestPhase;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.models.ContestData;
import fr.openmc.core.utils.bedrock.CharRemplacementUtils;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class ContestInfos implements InfoSwapper {

    @Override
    public boolean isActive() {
        if (WeeklyEventsManager.isEventActive() && WeeklyEventsManager.getCurrentEvent() instanceof Contest) {
            return WeeklyEventsManager.getCurrentPhase() != ContestPhase.VOTE_CAMP.getPhase();
        }
        return false;
    }

    @Override
    public void addInfosTo(Player player, List<Component> lines) {
        ContestData data = ContestManager.data;

        lines.add(MiniMessage.miniMessage().deserialize(
                "<gradient:#FFB800:#F0DF49><title></gradient>",
                Placeholder.component("title", TranslationManager.translation("feature.displays.scoreboard.contest.title", true))
        ).decoration(TextDecoration.BOLD, true));
        lines.add(text("  " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                .append(data.getCamp1ToSmall())
                .appendSpace()
                .append(TranslationManager.translation(player, "feature.displays.scoreboard.contest.vs", true).color(NamedTextColor.GRAY))
                .append(data.getCamp2ToSmall())
        );
        lines.add(Component.text("  " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                .append(TranslationManager.translation(player, "feature.displays.scoreboard.contest.ends", true).color(NamedTextColor.GRAY))
                .appendSpace()
                .append(text(DateUtils.getTimeUntilNextDay(DayOfWeek.MONDAY), TextColor.color(0xFF8F06)))
        );
    }
}
