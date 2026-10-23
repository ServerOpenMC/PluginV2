package fr.openmc.core.registry.regions.preset;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.registry.regions.CustomRegion;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import org.bukkit.World;

public class SquareRegion extends CustomRegion {

    public World world;
    public BlockVector3 pos1;
    public BlockVector3 pos2;

    @Getter
    public BlockVector3 min;

    @Getter
    public BlockVector3 max;

    public Key key;

    public SquareRegion(World world, BlockVector3 pos1, BlockVector3 pos2) {
        this(world, pos1, pos2, null);
    }

    public SquareRegion(World world, BlockVector3 pos1, BlockVector3 pos2, Key key) {
        this.world = world;
        min = pos1.getMinimum(pos2);
        max = pos1.getMaximum(pos2);
        this.pos1 = pos1;
        this.pos2 = pos2;
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
    public BlockVector3 getPos1() {
        return pos1;
    }

    @Override
    public BlockVector3 getPos2() {
        return pos2;
    }

    @Override
    public BlockVector3 getCenter() {
        return new BlockVector3(
                (min.x() - max.x()/2),
                (min.y() - max.y()/2),
                (min.z() - max.z()/2)
        );
    }
}
