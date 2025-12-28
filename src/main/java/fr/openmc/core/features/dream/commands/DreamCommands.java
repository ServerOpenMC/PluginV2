package fr.openmc.core.features.dream.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.DreamEndEvent;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;


public class DreamCommands {
    @Command("leave")
    @CommandPermission("omc.commands.dream.leave")
    public void leave(Player player) {
        if (!DreamUtils.isInDream(player)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'êtes pas dans un rêve"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getServer().getPluginManager().callEvent(new DreamEndEvent(player))
        );

        MessagesManager.sendMessage(player, Component.text("Vous avez quitté votre rêve avec succès."), Prefix.DREAM, MessageType.SUCCESS, false);
    }

    @Command("crafts")
    @CommandPermission("omc.commands.dream.crafts")
    public void crafts(Player player) {
        Bukkit.dispatchCommand(player, "itemsadder:ia omc_dream");
    }
}
