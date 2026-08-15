package fr.openmc.core.registry.worldtemplates.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.worldtemplates.WorldTemplate;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ForceBiomeOnTemplateWorldListener implements Listener {
    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event) {
        World world = event.getWorld();
        System.out.println("0");
        WorldTemplate template = OMCRegistry.WORLD_TEMPLATES.getByWorld(world);
        if (template == null) return;
        System.out.println("1");

        Biome templateBiome = template.getBiome();
        Chunk chunk = event.getChunk();

        if (world.getBiome(chunk.getX() << 4, 64, chunk.getZ() << 4) == templateBiome) return;

        System.out.println("3");
        fixChunkBiome(chunk, world, templateBiome);
    }

    private void fixChunkBiome(Chunk chunk, World world, Biome biome) {
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();
        int baseX = chunk.getX() << 4;
        int baseZ = chunk.getZ() << 4;

        for (int y = minY; y < maxY; y += 4) {
            for (int x = 0; x < 16; x += 4) {
                for (int z = 0; z < 16; z += 4) {
                    world.setBiome(baseX + x, y, baseZ + z, biome);
                }
            }
        }
    }
}
