package fr.openmc.core.features.displays.holograms.commands;

import fr.openmc.core.features.displays.holograms.HologramLoader;
import fr.openmc.core.features.displays.holograms.commands.autocomplete.HologramAutoComplete;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.IOException;

import static fr.openmc.core.features.displays.holograms.HologramLoader.hologramFolder;

@Command({"holograms", "holo", "hologram"})
public class HologramCommand {

    @Subcommand("setPos")
    @CommandPermission("op")
    @Description("Défini la position d'un Hologram.")
    void setPosCommand(
            Player player,
            @Named("hologramName") @SuggestWith(HologramAutoComplete.class) String hologramName
    ) {
        if (HologramLoader.displays.containsKey(hologramName)) {

            try {
                HologramLoader.setHologramLocation(hologramName, player.getLocation());
                MessagesManager.sendMessage(
                        player,
                        Component.text("§aPosition de l'hologramme " + hologramName + " mise à jour."),
                        Prefix.STAFF,
                        MessageType.SUCCESS,
                        true
                );
            } catch (IOException e) {
                MessagesManager.sendMessage(
                        player,
                        Component.text("§cErreur lors de la mise à jour de la position de l'hologramme " + hologramName + ": " + e.getMessage()),
                        Prefix.STAFF,
                        MessageType.ERROR,
                        true
                );
            }

        } else {
            String list = String.join(", ", HologramLoader.displays.keySet());
            MessagesManager.sendMessage(
                    player,
                    Component.text("§cVeuillez spécifier un hologramme valide : " + list),
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
                Component.text("§cHologrammes désactivés avec succès."),
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
                Component.text("§aHologrammes activés avec succès."),
                Prefix.STAFF,
                MessageType.SUCCESS,
                true
        );
    }
}
