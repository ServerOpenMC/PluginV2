package fr.openmc.core.features.dream.generation.listeners;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.generation.biomes.CloudChunkGenerator;
import fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator;
import fr.openmc.core.features.dream.mecanism.cloudcastle.BossCloudSpawner;
import fr.openmc.core.features.dream.mecanism.cloudcastle.CloudVault;
import fr.openmc.core.features.dream.mecanism.cloudcastle.PhantomCloudSpawner;
import fr.openmc.core.features.dream.mecanism.cloudcastle.StrayCloudSpawner;
import fr.openmc.core.features.dream.mecanism.tradernpc.GlaciteNpcManager;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.HashSet;
import java.util.Set;

public class ReplaceBlockListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!DreamUtils.isDreamWorld(event.getWorld())) return;

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            Set<ToReplace> toReplaces = new HashSet<>();
            ChunkSnapshot chunkSnapshot = event.getChunk().getChunkSnapshot();

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = GlaciteCaveChunkGenerator.MIN_CAVE_HEIGHT; y <= GlaciteCaveChunkGenerator.MAX_CAVE_HEIGHT; y++) {
                        if (chunkSnapshot.getBlockType(x, y, z) == Material.SEA_LANTERN) {
                            toReplaces.add(new ToReplace(x, y, z, Material.SEA_LANTERN));
                        }
                    }

                    for (int y = GlaciteCaveChunkGenerator.MAX_CAVE_HEIGHT; y <= CloudChunkGenerator.MIN_HEIGHT_CLOUD; y++) {
                        Material mat = chunkSnapshot.getBlockType(x, y, z);
                        if (mat.equals(Material.GRAY_GLAZED_TERRACOTTA)
                                || mat.equals(Material.TRIPWIRE)) {
                            toReplaces.add(new ToReplace(x, y, z, mat));
                        }
                    }

                    for (int y = CloudChunkGenerator.MAX_HEIGHT_CLOUD; y <= CloudChunkGenerator.MAX_HEIGHT_CLOUD + 85; y++) {
                        Material mat = chunkSnapshot.getBlockType(x, y, z);
                        if (mat.equals(Material.NETHERITE_BLOCK)
                                || mat.equals(Material.COAL_BLOCK)
                                || mat.equals(Material.LAPIS_BLOCK)
                                || mat.equals(Material.DIAMOND_BLOCK)) {
                            OMCPlugin.getInstance().getSLF4JLogger().info("y3 yes");
                            toReplaces.add(new ToReplace(x, y, z, mat));
                        }
                    }
                }
            }

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                for (ToReplace toReplace : toReplaces) {
                    Location blockLocation = new Location(
                            event.getWorld(),
                            toReplace.x,
                            toReplace.y,
                            toReplace.z
                    );
                    Block block = blockLocation.getBlock();
                    switch (toReplace.material) {
                        case SEA_LANTERN -> {
                            block.setType(Material.AIR);
                            GlaciteNpcManager.createNPC(blockLocation);
                        }
                        case TRIPWIRE -> CustomBlock.place("omc_dream:vegetation_1", blockLocation);
                        case NETHERITE_BLOCK -> BossCloudSpawner.replaceBlockWithBossCloudSpawner(block);
                        case COAL_BLOCK -> StrayCloudSpawner.replaceBlockWithMobCloudSpawner(block);
                        case LAPIS_BLOCK -> PhantomCloudSpawner.replaceBlockWithMobCloudSpawner(block);
                        case DIAMOND_BLOCK -> CloudVault.replaceBlockWithVault(block);
                    }
                }
            });
        });
    }

    public record ToReplace(int x, int y, int z, Material material) {
    }
}
