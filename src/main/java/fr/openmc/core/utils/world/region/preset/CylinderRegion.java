package fr.openmc.core.utils.world.region.preset;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.utils.world.region.Region;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class CylinderRegion extends Region {

    BlockVector3 cylCenter;
    World regionWorld;
    double radius;
    double minY;
    double maxY;

    public CylinderRegion(@NotNull BlockVector3 cylCenter, World world, double radius, double maxY) {
        this.cylCenter = cylCenter;
        this.radius = radius;
        this.minY = cylCenter.y();
        this.maxY = maxY;
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
        if (loc.getWorld() == null || regionWorld == null) return false;
        if (!loc.getWorld().equals(regionWorld)) return false;

        if (loc.getY() < minY || loc.getY() > maxY) return false;

        double dx = loc.getX() - cylCenter.x();
        double dz = loc.getZ() - cylCenter.z();
        double distanceSquared = dx * dx + dz * dz;

        return distanceSquared <= radius * radius;
    }

    @Override
    public World getWorld() {
        return regionWorld;
    }

    @Override
    public Set<Chunk> getChunks() {
        Set<Chunk> chunks = new HashSet<>();

        int minChunkX = (int) Math.floor((cylCenter.x() - radius) / 16);
        int maxChunkX = (int) Math.floor((cylCenter.x() + radius) / 16);
        int minChunkZ = (int) Math.floor((cylCenter.z() - radius) / 16);
        int maxChunkZ = (int) Math.floor((cylCenter.z() + radius) / 16);

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                chunks.add(regionWorld.getChunkAt(cx, cz));
            }
        }

        return chunks;
    }
}
