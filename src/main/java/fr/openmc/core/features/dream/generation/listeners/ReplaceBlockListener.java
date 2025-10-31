package fr.openmc.core.features.dream.generation.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.generation.biomes.CloudChunkGenerator;
import fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator;
import fr.openmc.core.features.dream.mecanism.cloudcastle.BossCloudSpawner;
import fr.openmc.core.features.dream.mecanism.cloudcastle.CloudVault;
import fr.openmc.core.features.dream.mecanism.cloudcastle.PhantomCloudSpawner;
import fr.openmc.core.features.dream.mecanism.cloudcastle.StrayCloudSpawner;
import fr.openmc.core.features.dream.registries.DreamBlocksRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.ArrayList;
import java.util.List;

public class ReplaceBlockListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        if (!DreamUtils.isDreamWorld(event.getWorld())) return;

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            List<Block> toReplace = new ArrayList<>();

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = GlaciteCaveChunkGenerator.MAX_CAVE_HEIGHT; y <= CloudChunkGenerator.MIN_HEIGHT_CLOUD; y++) {
                        Block block = chunk.getBlock(x, y, z);
                        if (block.getType() == Material.GRAY_GLAZED_TERRACOTTA) {
                            toReplace.add(block);
                        }
                    }

                    for (int y = CloudChunkGenerator.MAX_HEIGHT_CLOUD; y <= CloudChunkGenerator.MAX_HEIGHT_CLOUD + 85; y++) {
                        Block block = chunk.getBlock(x, y, z);
                        Material type = block.getType();
                        if (type == Material.NETHERITE_BLOCK || type == Material.COAL_BLOCK || type == Material.LAPIS_BLOCK || type == Material.DIAMOND_BLOCK) {
                            toReplace.add(block);
                        }
                    }
                }
            }

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                for (Block block : toReplace) {
                    switch (block.getType()) {
                        case GRAY_GLAZED_TERRACOTTA -> {
                            block.setType(Material.ENCHANTING_TABLE);
                            DreamBlocksRegistry.addDreamBlock("altar", block.getLocation());
                        }
                        case NETHERITE_BLOCK -> BossCloudSpawner.replaceBlockWithBossCloudSpawner(block);
                        case COAL_BLOCK -> StrayCloudSpawner.replaceBlockWithMobCloudSpawner(block);
                        case LAPIS_BLOCK -> PhantomCloudSpawner.replaceBlockWithMobCloudSpawner(block);
                        case DIAMOND_BLOCK -> CloudVault.replaceBlockWithVault(block);
                    }
                }
            });
        });
    }
}
