package fr.openmc.core.registry.ambient.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.ambient.BiomeAmbient;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.utils.nms.PlayerBiomeNMS;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listener qui va appliquer le biome de l'ambience d'un joueur si :
 * - le joueur a une ambience
 * - et que l'ambience utilise un biome ou non
 */
public class BiomesOnChunkLoad implements Listener {

    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event) {
        Player player = event.getPlayer();

        if (!CustomAmbient.ACTIVE_AMBIENTS.containsKey(player.getUniqueId())) return;

        CustomAmbient ambientApplied = OMCRegistry.CUSTOM_AMBIENTS.getOrThrow(
                CustomAmbient.ACTIVE_AMBIENTS.get(player.getUniqueId())
        );

        if (!(ambientApplied instanceof BiomeAmbient)) return;

        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        ChunkAccess chunkAccess = ((CraftChunk) event.getChunk()).getHandle(ChunkStatus.FULL);
        if (!(chunkAccess instanceof LevelChunk nmsChunk)) return;

        PlayerBiomeNMS.sendBiome(nmsPlayer, ambientApplied.CACHED_BIOME, nmsChunk);
    }
}
