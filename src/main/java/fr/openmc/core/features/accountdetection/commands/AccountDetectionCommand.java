package fr.openmc.core.features.accountdetection.commands;

import com.google.gson.JsonObject;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.accountdetection.AccountDetectionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.IOException;
import java.util.Objects;

import static fr.openmc.core.features.accountdetection.AccountDetectionManager.getVpnApiResponse;

@Command({"accountDetection"})
public class AccountDetectionCommand {
    @DefaultFor("~")
    void mainCommand(CommandSender sender) {
        sender.sendMessage("§cVeuillez spécifier une commande valide.");
    }

    @CommandPermission("omc.admin.commands.accountdetection.reload")
    @Subcommand("reload")
    void reloadCommand(CommandSender sender) {
        AccountDetectionManager.getInstance().reload();
        sender.sendMessage("§aLa configuration a été rechargée.");
    }

    @CommandPermission("omc.admin.commands.accountdetection.exemptplayer")
    @Subcommand("exemptePlayer")
    void exemptPlayerCommand(CommandSender sender, Player player) {
        try {
            AccountDetectionManager.getInstance().addExemptedPlayer(player.getUniqueId());
            sender.sendMessage("§aLe joueur " + player.getName() + " a été exempté.");
        } catch (IOException e) {
            sender.sendMessage("§cErreur lors de l'ajout du joueur à la liste des exemptés: " + e.getMessage());
        }
    }

    @CommandPermission("omc.admin.commands.accountdetection.check")
    @Subcommand("check")
    void checkCommand(CommandSender sender, Player player) {
        sender.sendMessage("Recherche en cours...");
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    JsonObject json = getVpnApiResponse(Objects.requireNonNull(player.getAddress()).getHostName());
                    if (json.get("is_vpn").getAsBoolean() ||
                            json.get("is_proxy").getAsBoolean() ||
                            json.get("is_datacenter").getAsBoolean() ||
                            json.get("is_tor").getAsBoolean() ||
                            json.get("is_abuser").getAsBoolean())
                        sender.sendMessage("Vpn détecté, voici la réponse de l'Api: " + json);
                    else
                        sender.sendMessage("Aucun Vpn détecté.");
                } catch (Exception e) {
                    sender.sendMessage("Impossible de vérifier l'adresse IP: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(OMCPlugin.getInstance());

    }
}
