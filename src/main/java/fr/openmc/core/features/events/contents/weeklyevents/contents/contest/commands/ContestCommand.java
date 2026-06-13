package fr.openmc.core.features.events.contents.weeklyevents.contents.contest.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.Contest;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.ContestPhase;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.commands.autocomplete.ColorContestAutoComplete;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.commands.autocomplete.ContestPhaseAutoComplete;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.commands.autocomplete.TradeContestAutoComplete;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestPlayerManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.TradeYMLManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.menu.ContributionMenu;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.menu.VoteMenu;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventPhase;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Command("contest")
@Description("Ouvre l'interface des festivals, et quand un festival commence, vous pouvez choisir votre camp")
public class ContestCommand {
    @Cooldown(4)
    @CommandPlaceholder()
    public static void mainCommand(Player player) {
        Contest contest = (Contest) WeeklyEventsManager.getEvent(Contest.class);

        if (!contest.isActive()) {
            int days = (ContestPhase.VOTE_CAMP.getPhase().getStartDay().getValue()
                    - DateUtils.getCurrentDayOfWeek().getValue() + 7) % 7;
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.events.contest.command.no_event",
                            Component.text(days).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.CONTEST, MessageType.ERROR, true);
            return;
        }

        WeeklyEventPhase activePhase = contest.getActivePhase();

        if (activePhase.equals(ContestPhase.VOTE_CAMP.getPhase())) {
            new VoteMenu(player).open();
        } else if (activePhase.equals(ContestPhase.TRADE_PHASE.getPhase())) {
            if (ContestManager.dataPlayer.get(player.getUniqueId()) != null) {
                new ContributionMenu(player).open();
            } else {
                new VoteMenu(player).open();
            }
        } else {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.events.contest.command.ended_phase"), Prefix.CONTEST, MessageType.ERROR, true);
        }
    }

    @Subcommand("setphase")
    @Description("Permet de lancer une procédure de phase")
    @CommandPermission("omc.admin.commands.contest.setphase")
    public void setPhase(@Named("phase") @SuggestWith(ContestPhaseAutoComplete.class) String phase) {
        WeeklyEventsManager.forceEventAtPhase(WeeklyEventsManager.getEvent(Contest.class), ContestPhase.valueOf(phase).getPhase());
    }

    @Subcommand("setcontest")
    @Description("Permet de définir un Contest")
    @CommandPermission("omc.admin.commands.contest.setcontest")
    public void setContest(
            Player player,
            @Named("nom du camp 1") String camp1,
            @Named("couleur du camp 1") @SuggestWith(ColorContestAutoComplete.class) String color1,
            @Named("nom du camp 2") String camp2,
            @Named("couleur du camp 2") @SuggestWith(ColorContestAutoComplete.class) String color2
    ) {
        Contest contest = (Contest) WeeklyEventsManager.getEvent(Contest.class);

        if (!contest.isActive()) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.events.contest.command.setcontest.not_active"), Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        if (contest.getActivePhase() != ContestPhase.VOTE_CAMP.getPhase()) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.events.contest.command.setcontest.started"), Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        // It is unique, but it is for performance reasons
        if (new HashSet<>(ContestManager.getColorContestList()).containsAll(Arrays.asList(color1, color2))) {
            ContestManager.clearDB();
            ContestManager.insertCustomContest(camp1, color1, camp2, color2);
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.events.contest.command.setcontest.saved",
                            Component.text(camp1).color(NamedTextColor.YELLOW),
                            Component.text(camp2).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.STAFF, MessageType.SUCCESS, true);
        } else {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.events.contest.command.setcontest.invalid_color"), Prefix.STAFF, MessageType.ERROR, true);
        }
    }

    @Subcommand("settrade")
    @Description("Permet de définir un Trade")
    @CommandPermission("omc.admin.commands.contest.settrade")
    public void setTrade(
            Player player,
            @Named("trade") @SuggestWith(TradeContestAutoComplete.class) String trade,
            @Named("tradeAmount") int amount,
            @Named("shellAmount") int amountShell
    ) {
        YamlConfiguration config = TradeYMLManager.getContestConfig();
        List<Map<?, ?>> trades = config.getMapList("contestTrades");

        boolean tradeFound = false;

        for (Map<?, ?> tradeEntry : trades) {
            if (tradeEntry.get("ress").equals(trade)) {
                ((Map<String, Object>) tradeEntry).put("amount", amount);
                ((Map<String, Object>) tradeEntry).put("amount_shell", amountShell);
                tradeFound = true;
                break;
            }
        }

        if (tradeFound) {
            TradeYMLManager.saveContestConfig();
            MessagesManager.sendMessage(player, Component.text("Le trade de " + trade + " a été mis à jour avec " + amount + " pour " + amountShell + " coquillages de contest."), Prefix.STAFF, MessageType.SUCCESS, true);
        } else {
            MessagesManager.sendMessage(player, Component.text("Le trade n'existe pas.\n/contest settrade <mat> <amount> <amount_shell>"), Prefix.STAFF, MessageType.ERROR, true);
        }
    }

    @Subcommand("addpoints")
    @Description("Permet d'ajouter des points a un membre")
    @CommandPermission("omc.admin.commands.contest.addpoints")
    public void addPoints(
            Player player,
            @Named("membre") @SuggestWith(OnlinePlayerAutoComplete.class) Player target,
            @Named("points") Integer points
    ) {
        Contest contest = (Contest) WeeklyEventsManager.getEvent(Contest.class);

        if (contest.getActivePhase() != ContestPhase.TRADE_PHASE.getPhase()) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.events.contest.command.addpoints.not_started"), Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        if (ContestManager.dataPlayer.get(target.getUniqueId()) == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.events.contest.command.addpoints.not_registered"), Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        if (points <= 0) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.events.contest.command.addpoints.invalid"), Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        ContestPlayerManager.setPointsPlayer(target.getUniqueId() ,points + ContestManager.dataPlayer.get(target.getUniqueId()).getPoints());
        MessagesManager.sendMessage(player,
                TranslationManager.translation(
                        "feature.events.contest.command.addpoints.success",
                        Component.text(points).color(NamedTextColor.YELLOW),
                        Component.text(target.getName()).color(NamedTextColor.YELLOW)
                ),
                Prefix.STAFF, MessageType.SUCCESS, true);
    }
}
