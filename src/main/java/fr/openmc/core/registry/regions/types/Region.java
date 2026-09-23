package fr.openmc.core.registry.regions.types;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.registry.regions.CustomRegion;

public abstract class Region extends CustomRegion {
    private final BlockVector3 min;
    private final BlockVector3 max;

    public Region() {
        this.max = getPos1().getMaximum(getPos2());
        this.min = getPos1().getMinimum(getPos2());
    }
    @Override
    public boolean contains(BlockVector3 pos) {
       return pos.containedWithin(
                min,
                max
        );
    }
}