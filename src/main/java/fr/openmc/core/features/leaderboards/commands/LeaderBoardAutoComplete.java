package fr.openmc.core.features.leaderboards.commands;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.leaderboards.LeaderBoardManager;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class LeaderBoardAutoComplete implements SuggestionProvider<BukkitCommandActor> {
    private final LeaderBoardManager leaderBoardManager = OMCRegistry.FEATURES.LEADERBOARD.get();

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return leaderBoardManager.getLeaderBoards()
                .filter(lb -> lb.startsWith(context.input().peekString())).toList();
    }
}
