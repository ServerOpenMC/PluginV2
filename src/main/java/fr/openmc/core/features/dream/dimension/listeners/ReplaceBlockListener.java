package fr.openmc.core.features.dream.dimension.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.mecanism.cloudcastle.BossCloudSpawner;
import fr.openmc.core.features.dream.mecanism.cloudcastle.CloudVault;
import fr.openmc.core.features.dream.mecanism.cloudcastle.PhantomCloudSpawner;
import fr.openmc.core.features.dream.mecanism.cloudcastle.StrayCloudSpawner;
import fr.openmc.core.features.dream.mecanism.tradernpc.GlaciteNpcManager;
import fr.openmc.core.features.dream.registries.DreamBlocksRegistry;
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
        Set<ToReplace> toReplaces = new HashSet<>();
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            ChunkSnapshot chunkSnapshot = event.getChunk().getChunkSnapshot();

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = -64; y <= 40; y++) {
                        if (chunkSnapshot.getBlockType(x, y, z) == Material.SEA_LANTERN) {
                            toReplaces.add(new ToReplace(x, y, z, Material.SEA_LANTERN)); // position GlaciteTrader NPC
                        }
                    }

                    for (int y = 40; y <= 120; y++) {
                        Material mat = chunkSnapshot.getBlockType(x, y, z);
                        if (mat.equals(Material.GRAY_GLAZED_TERRACOTTA)) {
                            toReplaces.add(new ToReplace(x, y, z, mat)); // soul altar
                        }
                    }

                    for (int y = 125; y <= 125 + 100; y++) {
                        Material mat = chunkSnapshot.getBlockType(x, y, z);
                        if (mat.equals(Material.NETHERITE_BLOCK)
                                || mat.equals(Material.COAL_BLOCK)
                                || mat.equals(Material.LAPIS_BLOCK)
                                || mat.equals(Material.DIAMOND_BLOCK)) {
                            toReplaces.add(new ToReplace(x, y, z, mat)); // spawner, vault
                        }
                    }
                }
            }

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                for (ToReplace toReplace : toReplaces) {
                    Location blockLocation = new Location(
                            event.getWorld(),
                            (chunkSnapshot.getX() << 4) + toReplace.x,
                            toReplace.y,
                            (chunkSnapshot.getZ() << 4) + toReplace.z
                    );
                    Block block = blockLocation.getBlock();

                    switch (toReplace.material) {
                        case GRAY_GLAZED_TERRACOTTA -> {
                            block.setType(Material.ENCHANTING_TABLE);
                            DreamBlocksRegistry.addDreamBlock("altar", block.getLocation());
                        }
                        case SEA_LANTERN -> {
                            block.setType(Material.AIR);
                            GlaciteNpcManager.createNPC(blockLocation);
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

    public record ToReplace(int x, int y, int z, Material material) {}
}
