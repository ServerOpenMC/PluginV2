package fr.openmc.core.utils.world.region.preset;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.utils.world.region.Region;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SquareRegion extends Region {

    @Getter BlockVector3 corner2;
    @Getter BlockVector3 corner1;
    @Getter World regionWorld;

    public SquareRegion(int x, int y, int z, int dx, int dy, int dz, World regionWorld) {
        this(new BlockVector3(x, y, z), new BlockVector3(dx, dy, dz), regionWorld);
    }

    public SquareRegion(@NotNull BlockVector3 corner1, @NotNull BlockVector3 corner2, World regionWorld) {

        this.corner1 = corner1;
        this.corner2 = corner2;

        this.regionWorld = regionWorld;
        super.initChunks();
    }

    // DEBUG //
//    public void addDetection() {
//        super.addDetection(
//                player -> true,
//                player -> player.sendMessage("test tu es entré dans la zone")
//        );
//    }

    public void addDetection(Consumer<Entity> action) {
        super.addDetection(entity -> true, action);
    }

    @Override
    public boolean isInside(Location loc) {
        if (loc == null || loc.getWorld() == null) return false;
        if (!loc.getWorld().equals(regionWorld)) return false;

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        return x >= Math.min(corner1.x(), corner2.x()) &&
                x <= Math.max(corner1.x(), corner2.x()) &&
                y >= Math.min(corner1.y(), corner2.y())  &&
                y <= Math.max(corner1.y(), corner2.y()) &&
                z >= Math.min(corner1.z(), corner2.z())  &&
                z <= Math.max(corner1.z(), corner2.z());
    }

    @Override
    public World getWorld() {
        return regionWorld;
    }

    @Override
    public Set<Chunk> getChunks() {

        Set<Chunk> chunks = new HashSet<>();

        double minX = Math.min(corner1.x(), corner2.x());
        double maxX = Math.max(corner1.x(), corner2.x());
        double minZ = Math.min(corner1.z(), corner2.z());
        double maxZ = Math.max(corner1.z(), corner2.z());

        int minChunkX = (int) Math.floor(minX / 16);
        int maxChunkX = (int) Math.floor(maxX / 16);
        int minChunkZ = (int) Math.floor(minZ / 16);
        int maxChunkZ = (int) Math.floor(maxZ / 16);

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                chunks.add(regionWorld.getChunkAt(cx, cz));
            }
        }

        return chunks;
    }

    @Override
    public SquareRegion clone() {
        try {
            return (SquareRegion) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}