package fr.openmc.core.utils.bukkit;

import fr.openmc.core.registry.items.keys.KeyBlock;
import org.bukkit.block.Block;

public class BlocksUtils {

    public static boolean isNearbyBlocks(KeyBlock keyBlock, Block origin, int diameter) {
        int radius = (diameter/2);
        int minusRadius = -radius;
        for (int x = minusRadius; x < radius; x++) {
            for (int y = minusRadius; y < radius; y++) {
                for (int z = minusRadius; z < radius; z++) {
                    if (keyBlock.matches(origin.getRelative(x, y, z))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
