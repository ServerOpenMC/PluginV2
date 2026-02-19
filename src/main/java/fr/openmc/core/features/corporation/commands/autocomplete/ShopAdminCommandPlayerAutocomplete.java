package fr.openmc.core.features.corporation.commands.autocomplete;

import fr.openmc.core.features.corporation.manager.ShopManager;
import fr.openmc.core.features.corporation.models.Shop;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ShopAdminCommandPlayerAutocomplete implements SuggestionProvider<BukkitCommandActor> {
	@Override
	public @NotNull Set<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
		Set<Shop> shops = ShopManager.getAllShops();
		return shops.stream()
				.map(shop -> CacheOfflinePlayer.getOfflinePlayer(shop.getOwnerUUID()).getName())
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}
}
