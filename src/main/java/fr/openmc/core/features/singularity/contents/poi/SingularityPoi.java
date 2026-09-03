package fr.openmc.core.features.singularity.contents.poi;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.singularity.contents.worldtemplates.SingularityWorldTemplate;
import fr.openmc.core.features.singularity.sub.world.SingularityWorldManager;
import fr.openmc.core.registry.poi.CustomPoi;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

public class SingularityPoi extends CustomPoi {
    @Override
    public Key getKey() {
        return Key.key("omc_singularity", "singularity");
    }

    @Override
    public BlockVector3 getPos1() {
        return BlockVector3.at(0, 0, 0);
    }

    @Override
    public BlockVector3 getPos2() {
        return BlockVector3.at(0, 0, 0);
    }

    @Override
    public World getWorld() {
        return OMCRegistry.WORLD_TEMPLATES.SINGULARITY_WORLD.getWorld();
    }
}
