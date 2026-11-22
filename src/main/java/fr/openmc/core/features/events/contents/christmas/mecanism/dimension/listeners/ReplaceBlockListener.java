package fr.openmc.core.features.events.contents.christmas.mecanism.dimension.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.ChristmasWorldUtils;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.GiftManager;
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
import java.util.concurrent.ThreadLocalRandom;

public class ReplaceBlockListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!ChristmasWorldUtils.isChristmasWorld(event.getWorld())) return;

        Set<ToReplace> toReplaces = new HashSet<>();
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            ChunkSnapshot chunkSnapshot = event.getChunk().getChunkSnapshot();

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = -64; y <= 200; y++) {
                        Material mat = chunkSnapshot.getBlockType(x, y, z);

                        // Coffres
                        if (mat == Material.CHISELED_SULFUR
                                || mat == Material.CHISELED_CINNABAR
                                || mat == Material.REINFORCED_DEEPSLATE
                                || mat == Material.HONEYCOMB_BLOCK
                        ) {
                            toReplaces.add(new ToReplace(x, y, z, mat));
                        } else if (mat == Material.ICE) {
                            toReplaces.add(new ToReplace(x, y, z, Material.ICE));
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
                        case CHISELED_SULFUR -> {
                            block.setType(Material.CHEST);
                            OMCRegistry.CUSTOM_LOOT_TABLES.SMALL_CHRISTMAS_CHEST.generateChestLoot(block);
                        }
                        case CHISELED_CINNABAR -> {

                            block.setType(Material.CHEST);
                            OMCRegistry.CUSTOM_LOOT_TABLES.MEDIUM_CHRISTMAS_CHEST.generateChestLoot(block);
                        }
                        case REINFORCED_DEEPSLATE -> {
                            block.setType(Material.CHEST);
                            OMCRegistry.CUSTOM_LOOT_TABLES.LARGE_CHRISTMAS_CHEST.generateChestLoot(block);
                        }
                        case HONEYCOMB_BLOCK -> {
                            block.setType(Material.CHEST);

                            double chance = ThreadLocalRandom.current().nextDouble();

                            if (chance < 0.5) {
                                OMCRegistry.CUSTOM_LOOT_TABLES.SMALL_CHRISTMAS_CHEST.generateChestLoot(block);
                            } else if (chance < 0.8) {
                                OMCRegistry.CUSTOM_LOOT_TABLES.MEDIUM_CHRISTMAS_CHEST.generateChestLoot(block);
                            } else {
                                OMCRegistry.CUSTOM_LOOT_TABLES.LARGE_CHRISTMAS_CHEST.generateChestLoot(block);
                            }
                        }
                        case ICE -> {
                            GiftManager.placeGiftInBlock(block);
                        }
                    }
                }
            });
        });
    }

    private record ToReplace(int x, int y, int z, Material material) { }
}
