package fr.openmc.core.features.city.commands.autocomplete;

import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static fr.openmc.core.features.city.CityManager.playerCities;

public class CityMembersAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        City city = playerCities.get(context.actor().requirePlayer().getUniqueId());

        if (city == null)
            return List.of();

        UUID cityUUID = city.getUniqueId();

        return playerCities.entrySet().stream()
                .filter(e -> e.getValue().getUniqueId().equals(cityUUID))
                .map(e -> CacheOfflinePlayer.getOfflinePlayer(e.getKey()))
                .filter(Objects::nonNull)
                .map(OfflinePlayer::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
