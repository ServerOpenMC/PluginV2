package fr.openmc.core.features.contest.commands;

import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.managers.ContestPlayerManager;
import fr.openmc.core.features.contest.managers.TradeYMLManager;
import fr.openmc.core.features.contest.menu.ContributionMenu;
import fr.openmc.core.features.contest.menu.VoteMenu;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Command("contest")
@Description("Ouvre l'interface des festivals, et quand un festival commence, vous pouvez choisir votre camp")
public class ContestCommand {
    @Cooldown(4)
    @DefaultFor("~")
    public static void mainCommand(Player player) {
        int phase = ContestManager.data.getPhase();
        if ((phase >= 2 && ContestManager.dataPlayer.get(player.getUniqueId()) == null) || (phase == 2)) {
            VoteMenu menu = new VoteMenu(player);
            menu.open();
        } else if (phase == 3 && ContestManager.dataPlayer.get(player.getUniqueId()) != null) {
            ContributionMenu menu = new ContributionMenu(player);
            menu.open();

        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E", Locale.FRENCH);
            DayOfWeek dayStartContestOfWeek = DayOfWeek.from(formatter.parse(ContestManager.data.getStartdate()));

            int days = (dayStartContestOfWeek.getValue() - DateUtils.getCurrentDayOfWeek().getValue() + 7) % 7;

            MessagesManager.sendMessage(player, Component.text("§cIl n'y a aucun Contest ! Revenez dans " + days + " jour(s)."), Prefix.CONTEST, MessageType.ERROR, true);
        }
    }

    @Subcommand("setphase")
    @Description("Permet de lancer une procédure de phase")
    @CommandPermission("omc.admin.commands.contest.setphase")
    public void setPhase(Integer phase) {
        switch(phase) {
            case 1:
                ContestManager.initPhase1();
                break;
            case 2:
                ContestManager.initPhase2();
                break;
            case 3:
                ContestManager.initPhase3();
                break;
            default:
                ContestManager.initPhase0();
                break;
        }
    }

    @Subcommand("setcontest")
    @Description("Permet de définir un Contest")
    @CommandPermission("omc.admin.commands.contest.setcontest")
    @AutoComplete("@colorContest")
    public void setContest(Player player, String camp1, @Named("colorContest") String color1, String camp2, @Named("colorContest") String color2) {
        int phase = ContestManager.data.getPhase();
        if (phase == 1) {
	        // It is unique, but it is for performance reasons
            if (new HashSet<>(ContestManager.getColorContestList()).containsAll(Arrays.asList(color1, color2))) {
                ContestManager.clearDB();
                ContestManager.insertCustomContest(camp1, color1, camp2, color2);

                MessagesManager.sendMessage(player, Component.text("§aLe Contest : " + camp1 + " VS " + camp2 + " a bien été sauvegardé\nMerci d'attendre que les données en cache s'actualise."), Prefix.STAFF, MessageType.SUCCESS, true);
            } else {
                MessagesManager.sendMessage(player, Component.text("§c/contest setcontest <camp1> <color1> <camp2> <color2> et color doit comporter une couleur valide"), Prefix.STAFF, MessageType.ERROR, true);
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("§cVous pouvez pas définir un contest lorsqu'il a commencé"), Prefix.STAFF, MessageType.ERROR, true);
        }
    }

    @Subcommand("settrade")
    @Description("Permet de définir un Trade")
    @CommandPermission("omc.admin.commands.contest.settrade")
    @AutoComplete("@trade")
    public void setTrade(Player player, @Named("trade") String trade, int amount, int amountShell) {
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
    public void addPoints(Player player, Player target, Integer points) {
        if (ContestManager.data.getPhase()!=3) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas donner des points lorsque le Contests n'a pas commencé"), Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        if (ContestManager.dataPlayer.get(target.getUniqueId()) == null) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas donner des points à ce joueur car il ne s'est pas inscrit"), Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        if (points<=0) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas donner des points négatifs ou égal à 0"), Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        ContestPlayerManager.setPointsPlayer(target.getUniqueId() ,points + ContestManager.dataPlayer.get(target.getUniqueId()).getPoints());
        MessagesManager.sendMessage(player, Component.text("§aVous avez ajouté " + points + " §apoint(s) à " + target.getName()), Prefix.STAFF, MessageType.SUCCESS, true);
    }
}
