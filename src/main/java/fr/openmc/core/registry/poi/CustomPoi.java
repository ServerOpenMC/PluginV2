package fr.openmc.core.registry.poi;

import com.sk89q.worldedit.math.BlockVector3;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

public abstract class CustomPoi {

    // * a @Override
    public void onFirstLoad() {}

    public void firstLoad() {
        onFirstLoad();
    }

    public abstract Key getKey();

    public abstract BlockVector3 getPos1();
    public abstract BlockVector3 getPos2();
    public abstract World getWorld();
}