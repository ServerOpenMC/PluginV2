package fr.openmc.core.hooks.github.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.hooks.github.GitHubHook;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class GithubLinkCommand {
    @Command("admingithub refresh")
    @CommandPermission("omc.admins.commands.admingithub.refresh")
    @Description("Force le rafraichissement du lien GitHub d'un joueur")
    private void refreshPlayerToGithubLink(Player executor,
                                           @Named("player") @SuggestWith(OnlinePlayerAutoComplete.class) Player target) {
        Long githubId = GitHubHook.refreshContributorId(target.getUniqueId());

        if (githubId == null) {
            MessagesManager.sendMessage(executor, TranslationManager.translation("hook.github.command.refresh.error",
                    Component.text(target.getName()).color(NamedTextColor.YELLOW)), Prefix.STAFF, MessageType.SUCCESS, true);
            return;
        }

        MessagesManager.sendMessage(executor, TranslationManager.translation("hook.github.command.refresh.success",
                Component.text(target.getName()).color(NamedTextColor.YELLOW),
                Component.text(githubId).color(NamedTextColor.YELLOW)), Prefix.STAFF, MessageType.SUCCESS, true);
    }
}
