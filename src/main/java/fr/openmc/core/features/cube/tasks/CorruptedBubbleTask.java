package fr.openmc.core.features.cube.tasks;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.cube.Cube;
import fr.openmc.core.features.cube.events.CubeDisableBubbleEvent;
import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class CorruptedBubbleTask extends BukkitRunnable {
    private final Cube cube;
    private final int radiusBubble;
    private final int totalTicks;
    private final int interval;
    private int elapsed = 0;

    public CorruptedBubbleTask(Cube cube, int radiusBubble, int intervalCorruption, int totalTicks) {
        this.cube = cube;
        this.radiusBubble = radiusBubble;
        this.totalTicks = totalTicks;
        this.interval = intervalCorruption;
    }

    @Override
    public void run() {
        if (elapsed >= totalTicks) {
            cancel();
            cube.corruptedBubbleTask = null;
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                    Bukkit.getPluginManager().callEvent(new CubeDisableBubbleEvent(cube)));
            return;
        }

        for (int i = 0; i < 30; i++) {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.random() * Math.PI;
            double r = Math.random() * radiusBubble;

            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.cos(phi);
            double z = r * Math.sin(phi) * Math.sin(theta);

            if (cube.isPartOf(new Location(cube.getCenter().getWorld(), x, y, z))) continue;

            Location loc = cube.getCenter().clone().add(x, y, z);
            Block block = loc.getBlock();
            Material type = block.getType();

            if (!DreamUtils.isDreamWorld(loc)) {
                switch (type) {
                    case DIRT, GRASS_BLOCK, SAND, GRAVEL -> block.setType(Material.WARPED_NYLIUM);
                    case OAK_LOG, BIRCH_LOG, SPRUCE_LOG, JUNGLE_LOG, DARK_OAK_LOG,
                         ACACIA_LOG, MANGROVE_LOG -> block.setType(Material.WARPED_STEM);
                    case AIR, LAPIS_BLOCK -> {
                    }
                    default -> block.setType(Material.SCULK);
                }
            } else {
                switch (type) {
                    case MUD -> block.setType(Material.SAND);
                    case AIR, LAPIS_BLOCK -> {
                    }
                    default -> block.setType(Material.GRASS_BLOCK);
                }
            }
        }

        elapsed += interval;
    }
}
