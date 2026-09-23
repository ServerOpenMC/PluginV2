package fr.openmc.core.features.city.menu.chunk;

import fr.openmc.core.utils.world.chunk.ChunkPos;

import java.util.Map;

public class ChunkDataCache {
    public static final long CACHE_EXPIRY_MS = 30000; // 30 seconds
    public final Map<ChunkPos, CityChunkInfo> chunkInfoMap;
    final long timestamp;

    public ChunkDataCache(Map<ChunkPos, CityChunkInfo> chunkInfoMap) {
        this.chunkInfoMap = chunkInfoMap;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_MS;
    }
}
