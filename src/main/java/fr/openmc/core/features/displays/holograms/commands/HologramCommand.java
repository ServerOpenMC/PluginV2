package fr.openmc.core.features.displays.holograms.commands;

import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.features.displays.holograms.HologramLoader;
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

import static fr.openmc.core.features.displays.holograms.HologramLoader.hologramFolder;

@Command({"holograms", "holo", "hologram"})
public class HologramCommand {

    @Subcommand("setPos")
    @CommandPermission("op")
    @Description("Défini la position d'un Hologram.")
    void setPosCommand(Player player, String hologramName) {
        if (HologramLoader.displays.containsKey(hologramName)) {
            ItemStack holoMoveItem = new ItemStack(Material.STICK);
            ItemMeta meta = holoMoveItem.getItemMeta();

            if (meta != null) {
                List<Component> info = new ArrayList<>();
                info.add(Component.text("§7Cliquez ou vous voulez pour poser l'hologramme"));
                info.add(Component.text("§cITEM POUR ADMIN"));
                meta.lore(info);
            }
            holoMoveItem.setItemMeta(meta);

            ItemInteraction.runLocationInteraction(
                    player,
                    holoMoveItem,
                    "admin:move-hologram",
                    120,
                    "Temps Restant : %sec%s",
                    "§cDéplacement de l'Hologramme " + hologramName,
                    holoMove -> {
                        if (holoMove == null) return true;
                        try {
                            HologramLoader.setHologramLocation(hologramName, holoMove);
                            MessagesManager.sendMessage(
                                    player,
                                    Component.text("§aPosition du hologramme " + hologramName + " mise à jour."),
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
            String list = String.join(", ", HologramLoader.displays.keySet());
            MessagesManager.sendMessage(
                    player,
                    Component.text("§cVeuillez spécifier un hologramme valide: " + list),
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
        MessagesManager.sendMessage(
                sender,
                Component.text("§cHolograms désactivés avec succès."),
                Prefix.STAFF,
                MessageType.SUCCESS,
                true
        );
    }

    @Subcommand("enable")
    @CommandPermission("op")
    @Description("Active tout")
    void enableCommand(CommandSender sender) {
        HologramLoader.updateHologramsViewers();
        HologramLoader.loadAllFromFolder(hologramFolder);
        MessagesManager.sendMessage(
                sender,
                Component.text("§aHolograms activés avec succès."),
                Prefix.STAFF,
                MessageType.SUCCESS,
                true
        );
    }
}
