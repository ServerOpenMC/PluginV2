package fr.openmc.core.features.city.sub.view;

import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.utils.world.chunk.ChunkPos;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.jetbrains.annotations.NotNull;

public record CityClaimViewData(ScheduledTask task, @NotNull Object2ObjectMap<ChunkPos, City> claims) {
}
