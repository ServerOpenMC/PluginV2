package fr.openmc.core.features.leaderboards.commands;

import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.leaderboards.command.invalid")
                .color(NamedTextColor.RED), Prefix.OPENMC, MessageType.ERROR, false);
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

    @Subcommand({"pumpkinCount"})
    @CommandPermission("omc.commands.leaderboard.money.pumpkin")
    @Description("Affiche le leaderboard des citrouilles des joueurs")
    void pumpkinCountCommand(CommandSender sender) {
        sender.sendMessage(createPumpkinCountTextLeaderboard());
    }


    @Subcommand("setPos")
    @CommandPermission("op")
    @Description("Défini la position d'un Hologram.")
    void setPosCommand(
            Player player,
            @Named("leaderboardName")
            @Suggest({"contributors", "money", "ville-money", "playtime", "pumpkin-count"})
            String leaderboard
    ) {
        if (leaderboard.equals("contributors") || leaderboard.equals("money") || leaderboard.equals("ville-money") || leaderboard.equals("playtime") || leaderboard.equals("pumpkin-count")) {
            try {
                LeaderboardManager.setHologramLocation(leaderboard, player.getLocation());
                MessagesManager.sendMessage(
                        player,
                        TranslationManager.translation(
                                "feature.leaderboards.command.position_updated",
                                Component.text(leaderboard).color(NamedTextColor.GREEN)
                        ).color(NamedTextColor.GREEN),
                        Prefix.STAFF,
                        MessageType.SUCCESS,
                        true
                );
            } catch (IOException e) {
                String errorMessage = e.getMessage() == null ? "" : e.getMessage();
                MessagesManager.sendMessage(
                        player,
                        TranslationManager.translation(
                                "feature.leaderboards.command.position_update_failed",
                                Component.text(leaderboard).color(NamedTextColor.RED),
                                Component.text(errorMessage).color(NamedTextColor.RED)
                        ).color(NamedTextColor.RED),
                        Prefix.STAFF,
                        MessageType.ERROR,
                        true
                );
            }
        } else {
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation("feature.leaderboards.command.invalid_list")
                            .color(NamedTextColor.RED),
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
        sender.sendMessage(TranslationManager.translation("feature.leaderboards.command.holograms_disabled")
                .color(NamedTextColor.RED));
    }

    @Subcommand("enable")
    @CommandPermission("op")
    @Description("Active tout")
    void enableCommand(CommandSender sender) {
        LeaderboardManager.enable();
        sender.sendMessage(TranslationManager.translation("feature.leaderboards.command.holograms_enabled")
                .color(NamedTextColor.GREEN));
    }

    @Subcommand("update")
    @CommandPermission("op")
    @Description("Met à jour les Holograms.")
    void updateCommand(CommandSender sender) {
        LeaderboardManager.updateGithubContributorsMap();
        LeaderboardManager.updatePlayerMoneyMap();
        LeaderboardManager.updateCityMoneyMap();
        LeaderboardManager.updatePlayTimeMap();
        LeaderboardManager.updatePumpkinCountMap();
        LeaderboardManager.updateHolograms();
        LeaderboardManager.updateHologramsViewers();
        sender.sendMessage(TranslationManager.translation("feature.leaderboards.command.holograms_updated")
                .color(NamedTextColor.GREEN));
    }

    @Subcommand("setScale")
    @CommandPermission("op")
    @Description("Défini la taille des Holograms.")
    void setScaleCommand(
            Player player,
            @Named("scale") float scale
    ) {
        Component scaleComponent = Component.text(Float.toString(scale)).color(NamedTextColor.GREEN);
        player.sendMessage(TranslationManager.translation(
                "feature.leaderboards.command.scale_changed",
                scaleComponent
        ).color(NamedTextColor.GREEN));
        try {
            LeaderboardManager.setScale(scale);
            player.sendMessage(TranslationManager.translation(
                    "feature.leaderboards.command.scale_changed",
                    scaleComponent
            ).color(NamedTextColor.GREEN));
        } catch (IOException e) {
            String errorMessage = e.getMessage() == null ? "" : e.getMessage();
            player.sendMessage(TranslationManager.translation(
                    "feature.leaderboards.command.scale_update_failed",
                    Component.text(errorMessage).color(NamedTextColor.RED)
            ).color(NamedTextColor.RED));
        }
    }
}
