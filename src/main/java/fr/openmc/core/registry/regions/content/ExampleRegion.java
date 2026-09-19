package fr.openmc.core.registry.regions.content;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.registry.regions.types.Region;
import net.kyori.adventure.key.Key;
import org.bukkit.World;

public class ExampleRegion extends Region {
    @Override
    public Key getKey() {
        return null;
    }

    @Override
    public World getWorld() {
        return null;
    }

    @Override
    public BlockVector3 getMin() {
        return null;
    }

    @Override
    public BlockVector3 getMax() {
        return null;
    }
}
