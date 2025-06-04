package fr.openmc.core.features.city;

import org.bukkit.Material;

import java.util.Set;

public record CityRanks(
		String name,
		Set<CPermission> permissions,
		byte priority,
		Material icon) {
}
