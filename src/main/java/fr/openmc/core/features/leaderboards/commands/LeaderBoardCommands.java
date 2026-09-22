package fr.openmc.core.features.leaderboards.commands;

import fr.openmc.core.features.leaderboards.LeaderBoard;
import fr.openmc.core.features.leaderboards.LeaderBoardManager;
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
import java.util.Optional;

@SuppressWarnings("unused")
@Command({"leaderboard", "lb"})
public class LeaderBoardCommands {
    @CommandPriority.Low
    @Subcommand("<leaderboardName>")
    void mainCommand(CommandSender sender,
                     @Named("leaderboardName")
                     @SuggestWith(LeaderBoardAutoComplete.class)
                     String leaderboard) {
            Optional<LeaderBoard> lb = LeaderBoardManager.getLeaderBoard(leaderboard);
            if (lb.isPresent()){
                MessagesManager.sendMessage(sender,lb.get().createComponent(), Prefix.OPENMC,MessageType.INFO,false);
                return;
            }
        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.leaderboards.command.invalid")
                .color(NamedTextColor.RED), Prefix.OPENMC, MessageType.ERROR, false);
    }

    @Subcommand("setPos <leaderboardName>")
    @CommandPermission("omc.admins.commands.leaderboard.setpos")
    @Description("Défini la position d'un Hologram.")
    void setPosCommand(
            Player player,
            @Named("leaderboardName")
            @SuggestWith(LeaderBoardAutoComplete.class)
            String leaderboard
    ) {
        Optional<LeaderBoard> lb = LeaderBoardManager.getLeaderBoard(leaderboard);
        if (lb.isPresent()) {
            try {
                lb.get().setLocation(player.getLocation());
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
    @CommandPermission("omc.admins.commands.leaderboard.disable")
    @Description("Désactive tout sauf les commandes")
    void disableCommand(CommandSender sender) {
        LeaderBoardManager.stop();
        sender.sendMessage(TranslationManager.translation("feature.leaderboards.command.holograms_disabled")
                .color(NamedTextColor.RED));
    }

    @Subcommand("enable")
    @CommandPermission("omc.admins.commands.leaderboard.enable")
    @Description("Active tout")
    void enableCommand(CommandSender sender) {
        LeaderBoardManager.start();
        sender.sendMessage(TranslationManager.translation("feature.leaderboards.command.holograms_enabled")
                .color(NamedTextColor.GREEN));
    }

    @Subcommand("update")
    @CommandPermission("omc.admins.commands.leaderboard.update")
    @Description("Met à jour les Holograms.")
    void updateCommand(CommandSender sender) {
        LeaderBoardManager.update();
        sender.sendMessage(TranslationManager.translation("feature.leaderboards.command.holograms_updated")
                .color(NamedTextColor.GREEN));
    }

    @Subcommand("reload")
    @CommandPermission("omc.admins.commands.leaderboard.reload")
    @Description("Recharge la configuration et les hologrammes.")
    void reloadCommand(CommandSender sender) {
        LeaderBoardManager.reload();
        sender.sendMessage(Component.text("Les leaderboards ont été rechargés.")
                .color(NamedTextColor.GREEN));
    }

    @Subcommand("setScale")
    @CommandPermission("omc.admins.commands.leaderboard.setscale")
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
            LeaderBoard.setScale(scale);
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
