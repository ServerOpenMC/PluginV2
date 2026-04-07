package fr.openmc.core.utils.world.chunk;

import fr.openmc.core.features.city.City;

public record ChunkInfo(City city, boolean isProtected) { }