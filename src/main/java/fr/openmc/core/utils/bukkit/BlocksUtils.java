package fr.openmc.core.utils.bukkit;

import fr.openmc.core.registry.items.keys.KeyBlock;
import org.bukkit.block.Block;

public class BlocksUtils {

    public static boolean isNearbyBlocks(KeyBlock keyBlock, Block origin, int radius) {
        for (int x = -(radius/2); x < radius/2; x++) {
            for (int y = -(radius/2); y < radius/2; y++) {
                for (int z = -(radius/2); z < radius/2; z++) {
                    if (keyBlock.matches(origin.getRelative(x, y, z))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
