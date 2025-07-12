package fr.openmc.core.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

@Getter
public class ChunkPos {

    final int x, z;
    public ChunkPos(int x, int z) { this.x = x; this.z = z; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkPos chunkPos = (ChunkPos) o;
        return x == chunkPos.x && z == chunkPos.z;
    }

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
        return Math.sqrt(distanceSq(other));
    }

    /**
     * Get the distance between this vector and another vector, squared.
     *
     * @param other the other vector
     * @return distance
     */
    public int distanceSq(ChunkPos other) {
        int dx = other.x - x;
        int dz = other.z - z;
        return dx * dx + dz * dz;
    }

    @Override
    public int hashCode() {
        return 31 * x + z;
    }

    @Override
    public String toString() {
        return x + "," + z;
    }
}
