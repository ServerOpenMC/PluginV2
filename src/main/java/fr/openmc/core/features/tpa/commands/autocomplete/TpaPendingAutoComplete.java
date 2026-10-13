package fr.openmc.core.features.tpa.commands.autocomplete;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.tpa.TPAManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class TpaPendingAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return TPAManager.getRequesters(OMCPlayer.of(context.actor().requirePlayer())).stream()
                .map(Player::getName)
                .toList();
    }
}