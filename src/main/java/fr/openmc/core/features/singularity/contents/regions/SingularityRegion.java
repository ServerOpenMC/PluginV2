package fr.openmc.core.features.singularity.contents.regions;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.regions.types.Region;
import net.kyori.adventure.key.Key;
import org.bukkit.World;

public class SingularityRegion extends Region {
    @Override
    public Key getKey() {
        return Key.key("omc_singularity", "singularity");
    }

    @Override
    public World getWorld() {
        return OMCRegistry.WORLD_TEMPLATES.SINGULARITY_WORLD.getWorld();
    }

    @Override
    public BlockVector3 getPos1() {
        return BlockVector3.at(100, 35, 100);
    }

    @Override
    public BlockVector3 getPos2() {
        return BlockVector3.at(-100, 200, -100);
    }
}
