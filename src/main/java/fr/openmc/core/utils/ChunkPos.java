package fr.openmc.core.utils;

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

    @Override
    public int hashCode() {
        return 31 * x + z;
    }

    @Override
    public String toString() {
        return x + "," + z;
    }
}
