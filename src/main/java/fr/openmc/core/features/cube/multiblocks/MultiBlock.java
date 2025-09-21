package fr.openmc.core.features.cube.multiblocks;

import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public abstract class MultiBlock {
    public final Location origin;
    protected final int radius;
    protected final Material material;

    @Setter
    protected boolean vulnerable = false;

    public MultiBlock(Location origin, int radius, Material material) {
        this.origin = origin;
        this.radius = radius;
        this.material = material;
    }

    public abstract void build();

    public abstract void clear();

    public abstract boolean isPartOf(Location loc);

    public void onBlockBreak(BlockBreakEvent event) {
        if (isPartOf(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    public void onExplosion(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> isPartOf(block.getLocation()));
    }

    public void onExplosion(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> isPartOf(block.getLocation()));
    }

    public void onPiston(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (isPartOf(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    public void onPiston(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (isPartOf(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
