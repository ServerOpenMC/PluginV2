package fr.openmc.core.features.displays.holograms.commands;

import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.features.displays.holograms.HologramLoader;
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
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Command({"holograms", "holo", "hologram"})
public class HologramCommand {

    @Subcommand("setPos")
    @CommandPermission("op")
    @Description("Défini la position d'un Hologram.")
    void setPosCommand(Player player, String hologramName) {
        if (HologramLoader.displays.containsKey(hologramName)) {
            ItemStack leaderboardMoveItem = new ItemStack(Material.STICK);
            ItemMeta meta = leaderboardMoveItem.getItemMeta();

            if (meta != null) {
                List<Component> info = new ArrayList<>();
                info.add(Component.text("§7Cliquez ou vous voulez pour poser l'hologramme"));
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
                            HologramLoader.setHologramLocation(hologramName, leaderboardMove);
                            MessagesManager.sendMessage(
                                    player,
                                    Component.text("§aPosition du leaderboard " + hologramName + " mise à jour."),
                                    Prefix.STAFF,
                                    MessageType.SUCCESS,
                                    true
                            );
                        } catch (IOException e) {
                            MessagesManager.sendMessage(
                                    player,
                                    Component.text("§cErreur lors de la mise à jour de la position du hologram " + hologramName + ": " + e.getMessage()),
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
                    Component.text("§cVeuillez spécifier un leaderboard valide: "),
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
        HologramLoader.unloadAll();
        sender.sendMessage("§cHolograms désactivés avec succès.");
    }

    @Subcommand("enable")
    @CommandPermission("op")
    @Description("Active tout")
    void enableCommand(CommandSender sender) {
        LeaderboardManager.enable();
        sender.sendMessage("§aHolograms activés avec succès.");
    }
}
