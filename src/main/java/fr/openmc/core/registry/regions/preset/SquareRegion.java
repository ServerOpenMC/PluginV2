package fr.openmc.core.registry.regions.preset;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.registry.regions.CustomRegion;
import net.kyori.adventure.key.Key;
import org.bukkit.World;

public class SquareRegion extends CustomRegion {

    public World world;
    public BlockVector3 min;
    public BlockVector3 max;
    public Key key;

    public SquareRegion(World world, BlockVector3 min, BlockVector3 max) {
        this(world, min, max, null);
    }

    public SquareRegion(World world, BlockVector3 min, BlockVector3 max, Key key) {
        this.world = world;
        this.min = min;
        this.max = max;
        this.key = key;
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public boolean contains(BlockVector3 pos) {
        double x = pos.x();
        double y = pos.y();
        double z = pos.z();

        return x >= Math.min(getMin().x(), getMax().x()) &&
                x <= Math.max(getMin().x(), getMax().x()) &&
                y >= Math.min(getMin().y(), getMax().y())  &&
                y <= Math.max(getMin().y(), getMax().y()) &&
                z >= Math.min(getMin().z(), getMax().z())  &&
                z <= Math.max(getMin().z(), getMax().z());
    }

    @Override
    public BlockVector3 getMin() {
        return min;
    }

    @Override
    public BlockVector3 getMax() {
        return max;
    }
}
