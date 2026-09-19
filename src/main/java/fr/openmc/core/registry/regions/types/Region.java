package fr.openmc.core.registry.regions.types;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.registry.regions.CustomRegion;

public abstract class Region extends CustomRegion {
    @Override
    public boolean contains(BlockVector3 pos) {
        return pos.containedWithin(
                getMin(),
                getMax()
        );
    }
}