package fr.openmc.core.utils.world.block;

import org.bukkit.block.Block;

import java.util.UUID;

public record BlockPos(UUID world, int x, int y, int z) {
    public static BlockPos of(Block block) {
        return new BlockPos(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
    }
}
