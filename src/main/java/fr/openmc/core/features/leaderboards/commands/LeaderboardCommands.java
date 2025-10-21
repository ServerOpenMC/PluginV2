package fr.openmc.core.features.leaderboards.commands;

import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.IOException;

import static fr.openmc.core.features.leaderboards.LeaderboardManager.*;

@SuppressWarnings("unused")
@Command({"leaderboard", "lb"})
public class LeaderboardCommands {
    @CommandPlaceholder()
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


    @Subcommand("setPos")
    @CommandPermission("op")
    @Description("Défini la position d'un Hologram.")
    void setPosCommand(
            Player player,
            @Named("leaderboardName")
            @Suggest({"contributors", "money", "ville-money", "playtime"})
            String leaderboard
    ) {
        if (leaderboard.equals("contributors") || leaderboard.equals("money") || leaderboard.equals("ville-money") || leaderboard.equals("playtime")) {
            try {
                LeaderboardManager.setHologramLocation(leaderboard, player.getLocation());
                MessagesManager.sendMessage(
                        player,
                        Component.text("§aPosition du leaderboard " + leaderboard + " mise à jour."),
                        Prefix.STAFF,
                        MessageType.SUCCESS,
                        true
                );
            } catch (IOException e) {
                MessagesManager.sendMessage(
                        player,
                        Component.text("§cErreur lors de la mise à jour de la position du leaderboard " + leaderboard + ": " + e.getMessage()),
                        Prefix.STAFF,
                        MessageType.ERROR,
                        true
                );
            }
        } else {
            MessagesManager.sendMessage(
                    player,
                    Component.text("§cVeuillez spécifier un leaderboard valide : contributors, money, ville-money, playtime"),
                    Prefix.STAFF,
                    MessageType.WARNING,
                    true
            );
        }
    }

    @Subcommand("disable")
    @CommandPermission("op")
    @Description("Désactive tout sauf les commandes")
    void disableCommand(CommandSender sender) {
        LeaderboardManager.disable();
        sender.sendMessage("§cHologrammes désactivés avec succès.");
    }

    @Subcommand("enable")
    @CommandPermission("op")
    @Description("Active tout")
    void enableCommand(CommandSender sender) {
        LeaderboardManager.enable();
        sender.sendMessage("§aHologrammes activés avec succès.");
    }

    @Subcommand("update")
    @CommandPermission("op")
    @Description("Met à jour les Holograms.")
    void updateCommand(CommandSender sender) {
        LeaderboardManager.updateGithubContributorsMap();
        LeaderboardManager.updatePlayerMoneyMap();
        LeaderboardManager.updateCityMoneyMap();
        LeaderboardManager.updatePlayTimeMap();
        LeaderboardManager.updateHolograms();
        LeaderboardManager.updateHologramsViewers();
        sender.sendMessage("§aHologrammes mis à jour avec succès.");
    }

    @Subcommand("setScale")
    @CommandPermission("op")
    @Description("Défini la taille des Holograms.")
    void setScaleCommand(
            Player player,
            @Named("scale") float scale
    ) {
        player.sendMessage("§aTaille des hologrammes modifiée à " + scale);
        try {
            LeaderboardManager.setScale(scale);
            player.sendMessage("§aTaille des hologrammes modifiée à " + scale);
        } catch (IOException e) {
            player.sendMessage("§cErreur lors de la mise à jour de la taille des hologrammes: " + e.getMessage());
        }
    }
}
