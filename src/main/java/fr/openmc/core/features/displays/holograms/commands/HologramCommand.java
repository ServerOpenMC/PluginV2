package fr.openmc.core.features.displays.holograms.commands;

import fr.openmc.core.features.displays.holograms.HologramLoader;
import fr.openmc.core.features.displays.holograms.commands.autocomplete.HologramAutoComplete;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.IOException;

@Command({"holograms", "holo", "hologram"})
public class HologramCommand {

    private final HologramLoader hologramLoader;

    public HologramCommand(HologramLoader loader) {
        this.hologramLoader = loader;
    }

    @Subcommand("setPos")
    @CommandPermission("op")
    @Description("Défini la position d'un Hologram.")
    void setPosCommand(
            Player player,
            @Named("hologramName") @SuggestWith(HologramAutoComplete.class) String hologramName
    ) {
        if (hologramLoader.displays.containsKey(hologramName)) {

            try {
                hologramLoader.setHologramLocation(hologramName, player.getLocation());
                MessagesManager.sendMessage(
                        player,
                        TranslationManager.translation("feature.displays.holograms.command.setpos.success", Component.text(hologramName)),
                        Prefix.STAFF,
                        MessageType.SUCCESS,
                        true
                );
            } catch (IOException e) {
                MessagesManager.sendMessage(
                        player,
                        TranslationManager.translation(
                                "feature.displays.holograms.command.setpos.error",
                                Component.text(hologramName),
                                Component.text(e.getMessage())
                        ),
                        Prefix.STAFF,
                        MessageType.ERROR,
                        true
                );
            }

        } else {
            String list = String.join(", ", hologramLoader.displays.keySet());
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation("feature.displays.holograms.command.setpos.invalid", Component.text(list)),
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
        hologramLoader.unloadAll();
        MessagesManager.sendMessage(
                sender,
                TranslationManager.translation("feature.displays.holograms.command.disable"),
                Prefix.STAFF,
                MessageType.SUCCESS,
                true
        );
    }

    @Subcommand("enable")
    @CommandPermission("op")
    @Description("Active tout")
    void enableCommand(CommandSender sender) {
        hologramLoader.updateHologramsViewers();
        hologramLoader.loadAllFromFolder(hologramLoader.getHologramFolder());
        MessagesManager.sendMessage(
                sender,
                TranslationManager.translation("feature.displays.holograms.command.enable"),
                Prefix.STAFF,
                MessageType.SUCCESS,
                true
        );
    }
}
