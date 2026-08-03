package fr.openmc.core.features.discord.commands;

import fr.openmc.core.features.discord.DiscordLinkManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("link")
@Description("Permet de lier son compte Discord a son compte Minecraft")
@CommandPermission("omc.commands.discord.link")
public class LinkCommand {

    @CommandPlaceholder
    public void link(Player player) {
        if (DiscordLinkManager.isLinked(player.getUniqueId())) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.discord.already_linked"),
                    Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        String code = DiscordLinkManager.startLink(player);

        if (code == null) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.discord.api_error"
                    ),
                    Prefix.OPENMC, MessageType.ERROR, true);
        } else {
            Component codeComponent = Component.text(code)
                    .color(NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD)
                    .clickEvent(ClickEvent.copyToClipboard("/openmc link code:" + code))
                    .hoverEvent(HoverEvent.showText(
                            TranslationManager.translation("feature.discord.code.hover")
                    ));

            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.discord.code", codeComponent),
                    Prefix.OPENMC, MessageType.INFO, true);
        }
    }

}
