package fr.openmc.core.features.city.view;

import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.ChunkPos;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.jetbrains.annotations.NotNull;

public record CityViewData(ScheduledTask task, @NotNull Object2ObjectMap<ChunkPos, City> claims) {
}
