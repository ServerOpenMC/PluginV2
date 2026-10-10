package fr.openmc.core.features.city.commands.autocomplete;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CityMembersAutoComplete implements SuggestionProvider<BukkitCommandActor> {
    private final CityManager cityManager = OMCRegistry.FEATURES.CITY.get();

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        Map<UUID, City> playerCity = cityManager.getPlayerCities();
        UUID playerCityUUID = playerCity.get(context.actor().requirePlayer().getUniqueId()).getUniqueId();

        if (playerCityUUID == null)
            return List.of();

        return playerCity.keySet().stream()
                .filter(uuid -> playerCity.get(uuid).getUniqueId().equals(playerCityUUID))
                .map(uuid -> CacheOfflinePlayer.getOfflinePlayer(uuid).getName())
                .collect(Collectors.toList());
    }
}
