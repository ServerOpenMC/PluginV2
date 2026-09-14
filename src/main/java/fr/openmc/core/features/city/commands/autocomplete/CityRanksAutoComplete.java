package fr.openmc.core.features.city.commands.autocomplete;

import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.models.db.DBCityRank;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class CityRanksAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        City city = City.ofPlayer(context.actor().requirePlayer());
        if (city == null) return List.of();

        return city.getRanks().stream()
                .map(DBCityRank::getName)
                .toList();
    }
}
