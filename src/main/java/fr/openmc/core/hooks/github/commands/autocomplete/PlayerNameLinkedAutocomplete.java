package fr.openmc.core.hooks.github.commands.autocomplete;

import fr.openmc.core.hooks.github.GitHubHook;
import fr.openmc.core.hooks.github.models.DBGithubMinecraft;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class PlayerNameLinkedAutocomplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return GitHubHook.getKnownLinks().stream()
                .map(DBGithubMinecraft::getPlayerName)
                .toList();
    }
}
