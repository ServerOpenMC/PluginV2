package fr.openmc.core.features.displays.scoreboards.sb;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import fr.openmc.api.hooks.FancyNpcsHook;
import fr.openmc.api.hooks.LuckPermsHook;
import fr.openmc.api.hooks.WorldGuardHook;
import fr.openmc.api.scoreboard.SternalBoard;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.models.Contest;
import fr.openmc.core.features.displays.scoreboards.BaseScoreboard;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.halloween.managers.HalloweenManager;
import fr.openmc.core.utils.DateUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static fr.openmc.core.utils.messages.MessagesManager.textToSmall;
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
        Contest data = ContestManager.data;
        if (data.getPhase() != 1) {
            lines.add(MiniMessage.miniMessage().deserialize("<gradient:#FFB800:#F0DF49>%s</gradient>".formatted(textToSmall("contest"))).decoration(TextDecoration.BOLD, true));
            lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                    .append(text(textToSmall(data.getCamp1()), data.getColor1AsNamedTextColor()))
                    .append(text(textToSmall(" VS "), NamedTextColor.GRAY))
                    .append(text(textToSmall(data.getCamp2()), data.getColor2AsNamedTextColor()))
            );
            lines.add(Component.text("  • ", NamedTextColor.DARK_GRAY)
                    .append(Component.text(textToSmall("fin:"), NamedTextColor.GRAY))
                    .appendSpace()
                    .append(text(DateUtils.getTimeUntilNextDay(DayOfWeek.MONDAY), TextColor.color(0xFF8F06)))
            );
        }

        lines.add(empty());
        lines.add(getFooter());

        board.updateLines(lines);
    }

    public static List<Component> getDefaultLines(Player player) {
        Component rank = LuckPermsHook.isHasLuckPerms()
                ? Component.text(LuckPermsHook.getFormattedPAPIPrefix(player))
                : Component.text(textToSmall("aucun")).color(TextColor.color(0xFF1FCC));

        City city = CityManager.getPlayerCity(player.getUniqueId());
        City chunkCity = CityManager.getCityFromChunk(player.getChunk().getX(), player.getChunk().getZ());
        boolean isInRegion = WorldGuardHook.isRegionConflict(player.getLocation());
        String location = isInRegion ? "§6Région Protégée" : "Nature";
        location = (chunkCity != null) ? chunkCity.getName() : location;

        String balance = EconomyManager.getMiniBalance(player.getUniqueId());

        List<Component> lines = new ArrayList<>();

        lines.add(empty());
        lines.add(MiniMessage.miniMessage().deserialize("<gradient:#FF45B9:#FF1FCC>%s</gradient>".formatted(textToSmall(player.getName()))).decoration(TextDecoration.BOLD, true));
        lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                .append(text(textToSmall("rang:"), NamedTextColor.GRAY))
                .appendSpace()
                .append(rank)
        );
        lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                .append(text(textToSmall("ville:"), NamedTextColor.GRAY))
                .appendSpace()
                .append(text(textToSmall(city != null ? city.getName() : "Aucune")).color(TextColor.color(0xFF06DC)))
        );
        lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                .append(text(textToSmall("argent:"), NamedTextColor.GRAY))
                .appendSpace()
                .append(text(textToSmall(balance)).color(TextColor.color(0xFF06DC)))
                .appendSpace()
                .append(text(EconomyManager.getEconomyIcon()))
        );
        lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                .append(text(textToSmall("location:"), NamedTextColor.GRAY))
                .appendSpace()
                .append(text(textToSmall(location)).color(TextColor.color(0xFF06DC)))
        );

        if (FancyNpcsHook.isHasFancyNpc()) {
            NpcManager npcManager = FancyNpcsPlugin.get().getNpcManager();
            Npc halloweenNPC = npcManager.getNpc("halloween_pumpkin_deposit_npc");
            if (halloweenNPC != null) {
                String pumpkinCount = EconomyManager.getFormattedSimplifiedNumber(HalloweenManager.getPumpkinCount(player.getUniqueId()));
                lines.add(text("  • ", NamedTextColor.DARK_GRAY)
                        .append(text(textToSmall("citrouilles:"), NamedTextColor.GRAY))
                        .appendSpace()
                        .append(text(textToSmall(pumpkinCount)).color(TextColor.color(0xFF7518)))
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
