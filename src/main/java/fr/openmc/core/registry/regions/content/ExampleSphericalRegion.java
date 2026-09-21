package fr.openmc.core.registry.regions.content;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.registry.regions.types.SphericalRegion;
import net.kyori.adventure.key.Key;
import org.bukkit.World;

public class ExampleSphericalRegion extends SphericalRegion {

    @Override
    public BlockVector3 getCenter() {
        return null;
    }

    @Override
    public double getRadius() {
        return 0;
    }

    @Override
    public Key getKey() {
        return null;
    }

    @Override
    public World getWorld() {
        return null;
    }
}
