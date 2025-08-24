package fr.openmc.core.features.city.view;

import fr.openmc.core.features.city.City;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public record ViewData(int taskID, @NotNull Object2ObjectMap<Chunk, City> claims) {
}
