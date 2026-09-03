package fr.openmc.core.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import fr.openmc.core.bootstrap.hooks.Hooks;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import net.kyori.adventure.key.Key;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class WorldGuardHook extends Hooks {
    public static boolean isEnable() {
        return Hooks.isEnabled(WorldGuardHook.class);
    }

    @Override
    protected Set<String> getPluginsName() {
        return Collections.singleton("WorldGuard");
    }

    public static void registerWorldGuardRegion(Key key, World world, BlockVector3 pos1, BlockVector3 pos2) {
        if (world == null) return;

        RegionContainer container = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer();

        RegionManager manager = container.get(BukkitAdapter.adapt(world));

        if (manager == null) return;

        ProtectedRegion region = new ProtectedCuboidRegion(
                key.namespace() + ":" + key.value(),
                pos1,
                pos2
        );

        try {
            manager.addRegion(region);
            manager.save();
        } catch (StorageException e) {
            OMCLogger.errorFormatted("Un problème a été rencontrée durant le save de la region", e);
        }
    }

    public static boolean isRegionConflict(Location location) {
        if (!isEnable()) return false;

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld()));

        if(regions == null) return false;

        for(ProtectedRegion region : regions.getRegions().values()) {
            if(isInside(region, location)) return true;
        }

        return false;
    }

    /**
     * Retourne si la positon mise est dans une région
     */
    public static boolean isInside(ProtectedRegion region, Location location) {
        return region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static boolean doesChunkContainWGRegion(Chunk chunk) {
        if (!isEnable()) return false;

        org.bukkit.World world = chunk.getWorld();
        int minX = chunk.getX() << 4;
        int minZ = chunk.getZ() << 4;
        int maxX = minX + 15;
        int maxZ = minZ + 15;
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));
        if (regions == null) return false;

        Collection<ProtectedRegion> chunkRegion = Collections.singleton(new ProtectedCuboidRegion(
                "__temp_check__",
                BlockVector3.at(minX, minY, minZ),
                BlockVector3.at(maxX, maxY, maxZ)
        ));

        for (ProtectedRegion region : regions.getRegions().values()) {
            if (!region.getIntersectingRegions(chunkRegion).isEmpty() || region.getIntersectingRegions(chunkRegion).contains(region)) {
                return true;
            }
        }

        return false;
    }
}
