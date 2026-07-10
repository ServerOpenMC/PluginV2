package fr.openmc.core.features.displays.scoreboards.sb;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import fr.openmc.api.scoreboard.SternalBoard;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.displays.scoreboards.BaseScoreboard;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.halloween.managers.HalloweenManager;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.Contest;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.ContestPhase;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.models.ContestData;
import fr.openmc.core.hooks.FancyNpcsHook;
import fr.openmc.core.hooks.LuckPermsHook;
import fr.openmc.core.hooks.WorldGuardHook;
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
import java.util.ArrayList;
import java.util.List;

import static fr.openmc.core.utils.text.messages.MessagesManager.textToSmall;
import static fr.openmc.core.utils.text.messages.MessagesManager.textToSmallComponent;
import static net.kyori.adventure.text.Component.*;

public class MainScoreboard extends BaseScoreboard {
    @Override
    protected void updateTitle(Player player, SternalBoard board) {
        board.updateTitle(getTitle());
    }

    @Override
    public void update(Player player, SternalBoard board) {
        List<Component> lines = new ArrayList<>(getDefaultLines(player));

        // Contest
        if (WeeklyEventsManager.getCurrentEvent() instanceof Contest) {
            ContestData data = ContestManager.data;
            if (WeeklyEventsManager.getCurrentPhase() != ContestPhase.VOTE_CAMP.getPhase()) {
                lines.add(MiniMessage.miniMessage().deserialize(
                        "<gradient:#FFB800:#F0DF49><title></gradient>",
                        Placeholder.component("title", TranslationManager.translation("feature.displays.scoreboard.contest.title.to_small"))
                ).decoration(TextDecoration.BOLD, true));
                lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                        .append(textToSmallComponent(data.getCamp1()).color(data.getColor1AsNamedTextColor()))
                        .appendSpace()
                        .append(TranslationManager.translation("feature.displays.scoreboard.contest.vs.to_small").color(NamedTextColor.GRAY))
                        .append(textToSmallComponent(data.getCamp2()).color(data.getColor2AsNamedTextColor()))
                );
                lines.add(Component.text("  • ", NamedTextColor.DARK_GRAY)
                        .append(TranslationManager.translation("feature.displays.scoreboard.contest.ends.to_small").color(NamedTextColor.GRAY))
                        .appendSpace()
                        .append(text(DateUtils.getTimeUntilNextDay(DayOfWeek.MONDAY), TextColor.color(0xFF8F06)))
                );
            }
        }

        lines.add(empty());
        lines.add(getFooter());

        board.updateLines(lines);
    }

    public static List<Component> getDefaultLines(Player player) {
        Component rank = LuckPermsHook.isEnable()
                ? Component.text(LuckPermsHook.getFormattedPAPIPrefix(player))
                : TranslationManager.translation("feature.displays.scoreboard.rank.none.to_small").color(TextColor.color(0xFF1FCC));


        City city = CityManager.getPlayerCity(player.getUniqueId());
        City chunkCity = CityManager.getCityFromChunk(player.getChunk().getX(), player.getChunk().getZ());
        boolean isInRegion = WorldGuardHook.isRegionConflict(player.getLocation());
        Component location = isInRegion
                ? TranslationManager.translation("feature.displays.scoreboard.location.protected.to_small")
                : TranslationManager.translation("feature.displays.scoreboard.location.wilderness.to_small");
        location = (chunkCity != null) ? textToSmallComponent(chunkCity.getName()) : location;

        String balance = EconomyManager.getMiniBalance(player.getUniqueId());

        List<Component> lines = new ArrayList<>();

        lines.add(empty());
        lines.add(MiniMessage.miniMessage().deserialize("<gradient:#FF45B9:#FF1FCC>%s</gradient>".formatted(textToSmall(player.getName()))).decoration(TextDecoration.BOLD, true));
        lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                .append(TranslationManager.translation("feature.displays.scoreboard.rank.label.to_small").color(NamedTextColor.GRAY))
                .appendSpace()
                .append(rank)
        );
        lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                .append(TranslationManager.translation("feature.displays.scoreboard.city.label.to_small").color(NamedTextColor.GRAY))
                .appendSpace()
                .append(city != null
                        ? textToSmallComponent(city.getName()).color(TextColor.color(0xFF06DC))
                        : TranslationManager.translation("feature.displays.scoreboard.city.none.to_small").color(TextColor.color(0xFF06DC)))
        );
        lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                .append(TranslationManager.translation("feature.displays.scoreboard.balance.label.to_small").color(NamedTextColor.GRAY))
                .appendSpace()
                .append(textToSmallComponent(balance).color(TextColor.color(0xFF06DC)))
                .appendSpace()
                .append(text(EconomyManager.getEconomyIcon()))
        );
        lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                .append(TranslationManager.translation("feature.displays.scoreboard.location.label.to_small").color(NamedTextColor.GRAY))
                .appendSpace()
                .append(location.color(TextColor.color(0xFF06DC)))
        );

        if (FancyNpcsHook.isEnable()) {
            NpcManager npcManager = FancyNpcsPlugin.get().getNpcManager();
            Npc halloweenNPC = null;
            if (npcManager != null)
                halloweenNPC = npcManager.getNpc("halloween_pumpkin_deposit_npc");
            if (halloweenNPC != null) {
                String pumpkinCount = EconomyManager.getFormattedSimplifiedNumber(HalloweenManager.getPumpkinCount(player.getUniqueId()));
                lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                        .append(TranslationManager.translation("feature.displays.scoreboard.pumpkins.label.to_small").color(NamedTextColor.GRAY))
                        .appendSpace()
                        .append(textToSmallComponent(pumpkinCount).color(TextColor.color(0xFF7518)))
                );
            }
        }

        lines.add(newline());

        return lines;
    }

    @Override
    public boolean shouldDisplay(Player player) {
        return true; // Toujours afficher ce scoreboard par défaut
    }

    @Override
    public int priority() {
        return 0; // Priorité la plus basse
    }
}
