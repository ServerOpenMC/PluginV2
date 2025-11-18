package fr.openmc.core.features.city.commands.autocomplete;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.models.DBCityRank;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class CityRanksAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        City city = CityManager.getPlayerCity(context.actor().requirePlayer().getUniqueId());
        if (city == null) return List.of();

        return city.getRanks().stream()
                .map(DBCityRank::getName)
                .toList();
    }
}
