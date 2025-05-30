package fr.openmc.core.features.leaderboards.commands;

import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


    @Subcommand("setPos")
    @CommandPermission("op")
    @Description("Défini la position d'un Hologram.")
    void setPosCommand(Player player, String leaderboard) {
        if (leaderboard.equals("contributors") || leaderboard.equals("money") || leaderboard.equals("ville-money") || leaderboard.equals("playtime")) {
            ItemStack leaderboardMoveItem = new ItemStack(Material.STICK);
            ItemMeta meta = leaderboardMoveItem.getItemMeta();

            if (meta != null) {
                List<Component> info = new ArrayList<>();
                info.add(Component.text("§7Cliquez ou vous voulez pour poser le leaderboard"));
                info.add(Component.text("§cITEM POUR ADMIN"));
                meta.lore(info);
            }
            leaderboardMoveItem.setItemMeta(meta);

            ItemInteraction.runLocationInteraction(
                    player,
                    leaderboardMoveItem,
                    "admin:move-leaderboard",
                    120,
                    "Temps Restant : %sec%s",
                    "§cDéplacement du leaderboard",
                    leaderboardMove -> {
                        if (leaderboardMove == null) return true;
                        try {
                            LeaderboardManager.getInstance().setHologramLocation(leaderboard, leaderboardMove);
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
                        return true;
                    },
                    null
            );

        } else {
            MessagesManager.sendMessage(
                    player,
                    Component.text("§cVeuillez spécifier un leaderboard valide: contributors, money, ville-money, playtime"),
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
