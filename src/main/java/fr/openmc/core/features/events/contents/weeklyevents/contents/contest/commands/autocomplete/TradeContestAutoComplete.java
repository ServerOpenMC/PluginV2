package fr.openmc.core.features.events.contents.weeklyevents.contents.contest.commands.autocomplete;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.TradeYMLManager;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class TradeContestAutoComplete implements SuggestionProvider<BukkitCommandActor> {
    private final TradeYMLManager tradeYMLManager = OMCRegistry.CONTEST_FEATURES.TRADE_YML;
    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return tradeYMLManager.getRessListFromConfig();
    }
}
