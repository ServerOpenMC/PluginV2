package fr.openmc.core.features.tpa.commands.autocomplete;

import fr.openmc.core.features.tpa.TPAQueue;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class TpaPendingAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return TPAQueue.getRequesters(context.actor().requirePlayer()).stream()
                .map(Player::getName)
                .toList();
    }
}