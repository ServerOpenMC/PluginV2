package fr.openmc.core.features.discord.commands;

import fr.openmc.core.features.discord.DiscordLinkManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("unlink")
@Description("Délie son compte Discord de son compte Minecraft")
@CommandPermission("omc.commands.discord.unlink")
public class UnlinkCommand {

    @CommandPlaceholder
    public void unlink(Player player) {
        boolean success = DiscordLinkManager.unlink(player.getUniqueId());

        if (!success) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.discord.not_linked"),
                    Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        MessagesManager.sendMessage(player,
                TranslationManager.translation("feature.discord.unlink_success"),
                Prefix.OPENMC, MessageType.SUCCESS, true);
    }
}