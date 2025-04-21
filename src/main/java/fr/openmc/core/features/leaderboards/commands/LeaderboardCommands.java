package fr.openmc.core.features.leaderboards.commands;

import fr.openmc.core.features.leaderboards.LeaderboardManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.IOException;

import static fr.openmc.core.features.leaderboards.LeaderboardManager.*;

@Command({"leaderboard", "lb"})
public class LeaderboardCommands {
    @DefaultFor("~")
    void mainCommand(CommandSender sender) {
        sender.sendMessage("§cVeuillez spécifier un leaderboard valide. (Ex: /leaderboard contributeurs)");
    }

    @Subcommand({"contributeurs", "contributors"})
    @CommandPermission("omc.commands.leaderboard.contributors")
    @Description("Affiche le leaderboard des contributeurs GitHub")
    void contributorsCommand(CommandSender sender) {
        sender.sendMessage(createContributorsTextLeaderboard());
    }


    //TODO: Utiliser ItemInteraction
    @Subcommand("setPos")
    @CommandPermission("op")
    void setPosCommand(Player player, String leaderboard) {
        if (leaderboard.equals("contributors") || leaderboard.equals("money") || leaderboard.equals("ville-money") || leaderboard.equals("playtime")) {
            try {
                LeaderboardManager.getInstance().setHologramLocation(leaderboard, player.getLocation());
            } catch (IOException e) {
                player.sendMessage("§cErreur lors de la mise à jour de la position du leaderboard " + leaderboard + ": " + e.getMessage());
                return;
            }
            player.sendMessage("§aPosition du leaderboard " + leaderboard + " mise à jour.");
        } else {
            player.sendMessage("§cVeuillez spécifier un leaderboard valide: contributors, money, ville-money, playtime");

        }
    }

    @Subcommand({"argent", "money"})
    @CommandPermission("omc.commands.leaderboard.money.player")
    @Description("Affiche le leaderboard de l'argent des joueurs")
    void moneyCommand(CommandSender sender) {
        sender.sendMessage(createMoneyTextLeaderboard());
    }

    @Subcommand({"argent_ville", "city_money"})
    @CommandPermission("omc.commands.leaderboard.money.city")
    @Description("Affiche le leaderboard de l'argent des villes")
    void cityMoneyCommand(CommandSender sender) {
        sender.sendMessage(createCityMoneyTextLeaderboard());
    }

    @Subcommand({"temps_de_jeu", "playtime"})
    @CommandPermission("omc.commands.leaderboard.money.city")
    @Description("Affiche le leaderboard de l'argent des villes")
    void playtimeCommand(CommandSender sender) {
        sender.sendMessage(createPlayTimeTextLeaderboard());
    }
}
