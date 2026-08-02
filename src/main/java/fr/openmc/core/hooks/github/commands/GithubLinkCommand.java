package fr.openmc.core.hooks.github.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.hooks.github.GitHubHook;
import fr.openmc.core.hooks.github.commands.autocomplete.ContributorNameAutocomplete;
import fr.openmc.core.hooks.github.commands.autocomplete.PlayerNameLinkedAutocomplete;
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
    @Command("admingithub link")
    @CommandPermission("omc.admins.commands.admingithub.link")
    @Description("Lie un joueur à un contributeur GitHub")
    private void linkPlayerToContributor(Player executor,
                          @Named("player") @SuggestWith(OnlinePlayerAutoComplete.class) Player target,
                          @Named("contributorName") @SuggestWith(ContributorNameAutocomplete.class) String contributorName) {
        if (GitHubHook.getContributorLink(target.getUniqueId()) != null) {
            MessagesManager.sendMessage(executor, TranslationManager.translation("hook.github.command.link.player_already_linked"),
                    Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        long contributorId = GitHubHook.getContributorId(contributorName);

        if (contributorId == -1) {
            MessagesManager.sendMessage(executor, TranslationManager.translation("hook.github.command.link.contributor_not_found"),
                    Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        if (GitHubHook.getPlayerLinkTo(contributorId) != null) {
            MessagesManager.sendMessage(executor, TranslationManager.translation("hook.github.command.link.github_already_linked"),
                    Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        GitHubHook.linkPlayerToContributor(target.getUniqueId(), contributorId);
        MessagesManager.sendMessage(executor, TranslationManager.translation("hook.github.command.link.success",
                Component.text(target.getName()).color(NamedTextColor.YELLOW),
                Component.text(contributorName).color(NamedTextColor.YELLOW)), Prefix.STAFF, MessageType.SUCCESS, true);
    }

    @Command("admingithub unlink")
    @CommandPermission("omc.admins.commands.admingithub.unlink")
    @Description("Délie un joueur à un contributeur GitHub")
    private void unlinkPlayerToContributor(Player executor,
                                         @Named("player lié") @SuggestWith(PlayerNameLinkedAutocomplete.class) Player targetLinked) {
        if (GitHubHook.getContributorLink(targetLinked.getUniqueId()) == null) {
            MessagesManager.sendMessage(executor, TranslationManager.translation("hook.github.command.unlink.player_not_linked"),
                    Prefix.STAFF, MessageType.ERROR, true);
            return;
        }

        GitHubHook.unlinkPlayerToContributor(targetLinked.getUniqueId());
        MessagesManager.sendMessage(executor, TranslationManager.translation("hook.github.command.unlink.success",
                Component.text(targetLinked.getName()).color(NamedTextColor.YELLOW)), Prefix.STAFF, MessageType.SUCCESS, true);
    }
}
