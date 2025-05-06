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

    @Subcommand({"contributeurs"})
    @CommandPermission("omc.commands.leaderboard.contributors")
    @Description("Affiche le leaderboard des contributeurs GitHub")
    void contributorsCommand(CommandSender sender) {
        sender.sendMessage(createContributorsTextLeaderboard());
    }

    @Subcommand({"argent"})
    @CommandPermission("omc.commands.leaderboard.money.player")
    @Description("Affiche le leaderboard de l'argent des joueurs")
    void moneyCommand(CommandSender sender) {
        sender.sendMessage(createMoneyTextLeaderboard());
    }

    @Subcommand({"cityMoney"})
    @CommandPermission("omc.commands.leaderboard.money.city")
    @Description("Affiche le leaderboard de l'argent des villes")
    void cityMoneyCommand(CommandSender sender) {
        sender.sendMessage(createCityMoneyTextLeaderboard());
    }

    @Subcommand({"playtime"})
    @CommandPermission("omc.commands.leaderboard.money.playtime")
    @Description("Affiche le leaderboard du temps de jeu des joueurs")
    void playtimeCommand(CommandSender sender) {
        sender.sendMessage(createPlayTimeTextLeaderboard());
    }


    //TODO: Utiliser ItemInteraction (Iambibi)
    @Subcommand("setPos")
    @CommandPermission("op")
    @Description("Défini la position d'un Hologram.")
    void setPosCommand(Player player, String leaderboard) {
        if (leaderboard.equals("contributors") || leaderboard.equals("money") || leaderboard.equals("ville-money") || leaderboard.equals("playtime")) {
            try {
                LeaderboardManager.getInstance().setHologramLocation(leaderboard, player.getLocation());
                player.sendMessage("§aPosition du leaderboard " + leaderboard + " mise à jour.");
            } catch (IOException e) {
                player.sendMessage("§cErreur lors de la mise à jour de la position du leaderboard " + leaderboard + ": " + e.getMessage());
            }
        } else {
            player.sendMessage("§cVeuillez spécifier un leaderboard valide: contributors, money, ville-money, playtime");
        }
    }

    @Subcommand("disable")
    @CommandPermission("op")
    @Description("Désactive tout sauf les commandes")
    void disableCommand(CommandSender sender) {
        LeaderboardManager.getInstance().disable();
    }

    @Subcommand("enable")
    @CommandPermission("op")
    @Description("Active tout")
    void enableCommand(CommandSender sender) {
        LeaderboardManager.getInstance().enable();
    }

    @Subcommand("setScale")
    @CommandPermission("op")
    @Description("Défini la taille des Holograms.")
    void setScaleCommand(Player player, float scale) {
        player.sendMessage("§aTaille des Holograms modifiée à " + scale);
        try {
            LeaderboardManager.getInstance().setScale(scale);
            player.sendMessage("§aTaille des Holograms modifiée à " + scale);
        } catch (IOException e) {
            player.sendMessage("§cErreur lors de la mise à jour de la taille des holograms: " + e.getMessage());
        }
    }
}
