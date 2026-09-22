package fr.openmc.core.registry.regions.types;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.registry.regions.CustomRegion;

public abstract class SphericalRegion extends CustomRegion {

    public abstract BlockVector3 getCenter();

    public abstract double getRadius();

    @Override
    public boolean contains(BlockVector3 pos) {
        double dx = pos.x() - getCenter().x();
        double dz = pos.z() - getCenter().z();
        double distanceSquared = dx * dx + dz * dz;

        return distanceSquared <= getRadius() * getRadius();
    }

    @Override
    public BlockVector3 getPos1() {
        return null;
    }

    @Override
    public BlockVector3 getPos2() {
        return null;
    }
}
