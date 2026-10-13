package fr.openmc.core.features.city.models.city.namespaces;

import fr.openmc.core.utils.world.chunk.ChunkPos;

import java.util.Set;

public interface CityChunks {
    int getFreeClaims();

    void updateFreeClaims(int diff);

    Set<ChunkPos> getChunks();

    void addChunk(int x, int z);

    void removeChunk(int x, int z);

    boolean hasChunk(int x, int z);
}
