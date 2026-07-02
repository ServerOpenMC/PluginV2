package fr.openmc.core.features.dream.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.DreamEndEvent;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;


public class DreamCommands {
    @Command("leave")
    @CommandPermission("omc.commands.dream.leave")
    public void leave(Player player) {
        if (!DreamUtils.isInDream(player)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.commands.leave.not_in_dream"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getServer().getPluginManager().callEvent(new DreamEndEvent(player))
        );

        MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.commands.leave.success"), Prefix.DREAM, MessageType.SUCCESS, false);
    }

    @Command("crafts")
    @CommandPermission("omc.commands.dream.crafts")
    public void crafts(Player player) {
        Bukkit.dispatchCommand(player, "itemsadder:ia omc_dream");
    }
}
