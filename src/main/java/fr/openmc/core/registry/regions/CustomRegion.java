package fr.openmc.core.registry.regions;

import com.sk89q.worldedit.math.BlockVector3;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class CustomRegion {
    public abstract Key getKey();
    public abstract World getWorld();

    public abstract boolean contains(BlockVector3 pos);
    public abstract BlockVector3 getMin();
    public abstract BlockVector3 getMax();

    public final boolean contains(Location loc) {
        if (loc == null || loc.getWorld() == null) return false;
        if (getWorld() == null) return false;
        if (!loc.getWorld().getName().equals(getWorld().getName())) return false;

        return contains(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()));
    }
}
