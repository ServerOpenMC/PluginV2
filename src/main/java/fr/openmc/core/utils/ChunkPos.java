package fr.openmc.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public record ChunkPos(int x, int z) {

    public Chunk getChunkInWorld() {
        World world = Bukkit.getWorld("world");
        if (world == null) {
            throw new IllegalStateException("World 'world' does not exist.");
        }

        return world.getChunkAt(x, z);
    }

    /**
     * Get the distance between this vector and another vector.
     *
     * @param other the other vector
     * @return distance
     */
    public double distance(ChunkPos other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Get the distance between this vector and another vector, squared.
     *
     * @param other the other vector
     * @return distance
     */
    public int distanceSquared(ChunkPos other) {
        int dx = other.x - x;
        int dz = other.z - z;
        return dx * dx + dz * dz;
    }
}
