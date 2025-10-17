package fr.openmc.core.features.dream.listeners.generation;

import fr.openmc.core.features.dream.blocks.DreamBlocksManager;
import fr.openmc.core.features.dream.blocks.cloudspawner.MobCloudSpawner;
import fr.openmc.core.features.dream.blocks.cloudvault.CloudVault;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.generation.biomes.CloudChunkGenerator;
import fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ReplaceBlockListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        if (!event.getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = GlaciteCaveChunkGenerator.MAX_CAVE_HEIGHT; y <= CloudChunkGenerator.MIN_HEIGHT_CLOUD; y++) {
                    Block block = chunk.getBlock(x, y, z);
                    if (block.getType() == Material.GRAY_GLAZED_TERRACOTTA) {
                        block.setType(Material.ENCHANTING_TABLE);
                        DreamBlocksManager.addDreamBlock("altar", block.getLocation());
                    }
                }
                for (int y = CloudChunkGenerator.MAX_HEIGHT_CLOUD; y <= CloudChunkGenerator.MAX_HEIGHT_CLOUD + 85; y++) {
                    Block block = chunk.getBlock(x, y, z);
                    switch (block.getType()) {
                        case COAL_BLOCK -> MobCloudSpawner.replaceBlockWithMobCloudSpawner(block);
                        case DIAMOND_BLOCK -> CloudVault.replaceBlockWithVault(block);
                    }
                }
            }
        }
    }
}
