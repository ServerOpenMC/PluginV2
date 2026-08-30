package fr.openmc.core.features.displays.scoreboards.sb;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import fr.openmc.api.scoreboard.SternalBoard;
import fr.openmc.core.features.bits.BitsManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.corpse.CorpseManager;
import fr.openmc.core.features.corpse.npc.CorpseNPC;
import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
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
import fr.openmc.core.utils.bedrock.CharRemplacementUtils;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.fonts.SmallCapsUtils;
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

import static fr.openmc.core.utils.text.fonts.SmallCapsUtils.toSmall;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

public class MainScoreboard extends BaseScoreboard {
    @Override
    protected void updateTitle(Player player, SternalBoard board) {
        board.updateTitle(getTitle());
    }

    @Override
    public void update(Player player, SternalBoard board) {
        List<Component> lines = new ArrayList<>(getDefaultLines(player, false));

        // Corpse
        if (CorpseNPCManager.getNPC(player.getUniqueId()) instanceof CorpseNPC corpse) {

            lines.add(MiniMessage.miniMessage().deserialize(
                    "<gradient:#F82C5D:#F64545><title></gradient>",
                    Placeholder.component("title", TranslationManager.translation("feature.displays.scoreboard.corpse.title")))
                    .font(SmallCapsUtils.SMALL_CAPS_FONT)
                    .decoration(TextDecoration.BOLD, true)
                    .appendSpace()
                    .append(CorpseManager.getCorpseDirection(player, corpse))
                    .appendSpace()
                    .append(CorpseManager.getRemainingTime(player.getUniqueId()))
            );
        }

        // Contest
        if (WeeklyEventsManager.isEventActive() &&
                WeeklyEventsManager.getCurrentEvent() instanceof Contest) {
            ContestData data = ContestManager.data;
            if (WeeklyEventsManager.getCurrentPhase() != ContestPhase.VOTE_CAMP.getPhase()) {
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

        lines.add(empty());
        lines.add(getFooter());

        board.updateLines(lines);
    }

    public static List<Component> getDefaultLines(Player player, boolean inWar) {
        Component rank = LuckPermsHook.isEnable()
                ? Component.text(LuckPermsHook.getFormattedPAPIPrefix(player))
                : TranslationManager.translation("feature.displays.scoreboard.rank.none", true).color(TextColor.color(0xFF1FCC));


        City city = CityManager.getPlayerCity(player.getUniqueId());
        City chunkCity = CityManager.getCityFromChunk(player.getChunk().getX(), player.getChunk().getZ());
        boolean isInRegion = WorldGuardHook.isRegionConflict(player.getLocation());
        Component location = isInRegion
                ? TranslationManager.translation("feature.displays.scoreboard.location.protected", true)
                : TranslationManager.translation("feature.displays.scoreboard.location.wilderness", true);
        location = (chunkCity != null) ? toSmall(player, chunkCity.getName()) : location;

        String balance = EconomyManager.getMiniBalance(player.getUniqueId());
        double bits = BitsManager.getBits(player.getUniqueId());

        List<Component> lines = new ArrayList<>();

        lines.add(empty());
        lines.add(MiniMessage.miniMessage().deserialize(
                "<gradient:#FF45B9:#FF1FCC><font:omc_fonts:small_caps>%s</font></gradient>".formatted(
                        player.getName())).decoration(TextDecoration.BOLD, true));
        lines.add(text("  " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                .append(TranslationManager.translation(player, "feature.displays.scoreboard.rank.label", true).color(NamedTextColor.GRAY))
                .appendSpace()
                .append(rank)
        );
        lines.add(text("  " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                .append(TranslationManager.translation(player, "feature.displays.scoreboard.city.label", true).color(NamedTextColor.GRAY))
                .appendSpace()
                .append(city != null
                        ? toSmall(player, city.getName()).color(TextColor.color(0xFF06DC))
                        : TranslationManager.translation(player, "feature.displays.scoreboard.city.none", true).color(TextColor.color(0xFF06DC)))
        );
        if (!inWar) {
            lines.add(text("  " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                    .append(TranslationManager.translation(player, "feature.displays.scoreboard.balance.label", true).color(NamedTextColor.GRAY))
                    .appendSpace()
                    .append(toSmall(player, balance).color(TextColor.color(0xFF06DC)))
                    .appendSpace()
                    .append(text(EconomyManager.getEconomyIcon()))
            );
            if (bits > 0) {
                lines.add(text("  " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                        .append(TranslationManager.translation(player, "feature.displays.scoreboard.bits.label", true).color(NamedTextColor.GRAY))
                        .appendSpace()
                        .append(toSmall(player, EconomyManager.getFormattedSimplifiedNumber(bits)).color(TextColor.color(0x07A0F5)))
                        .appendSpace()
                        .append(text(BitsManager.getBitsIcon()))
                );
            }
        }
        lines.add(text("  " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                .append(TranslationManager.translation(player, "feature.displays.scoreboard.location.label", true).color(NamedTextColor.GRAY))
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
                lines.add(text("  " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                        .append(TranslationManager.translation(player, "feature.displays.scoreboard.pumpkins.label", true).color(NamedTextColor.GRAY))
                        .appendSpace()
                        .append(toSmall(player, pumpkinCount).color(TextColor.color(0xFF7518)))
                );
            }
        }

        lines.add(empty());

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
