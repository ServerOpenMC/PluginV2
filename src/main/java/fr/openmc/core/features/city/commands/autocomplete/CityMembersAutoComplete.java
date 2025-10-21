package fr.openmc.core.features.city.commands.autocomplete;

import fr.openmc.core.utils.CacheOfflinePlayer;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static fr.openmc.core.features.city.CityManager.playerCities;

public class CityMembersAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        UUID playerCityUUID = playerCities.get(context.actor().requirePlayer().getUniqueId()).getUniqueId();

        if (playerCityUUID == null)
            return List.of();

        return playerCities.keySet().stream()
                .filter(uuid -> playerCities.get(uuid).getUniqueId().equals(playerCityUUID))
                .map(uuid -> CacheOfflinePlayer.getOfflinePlayer(uuid).getName())
                .collect(Collectors.toList());
    }
}
