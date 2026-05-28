package fr.openmc.core.utils.nms;

import fr.openmc.core.registry.ambient.CustomAmbient;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.LevelData;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;

/**
 * Classe receuillant les NMS lié au packet {@link ClientboundRespawnPacket}
 * Afin de simplifier l'utilisation des NMS
 *
 * @see CustomAmbient utilisation trés spécifique, on affiche une dimension_type sur le joueur, ds la dimension actuelle
 */
public class PlayerRespawnNMS {
    // todo: continue this
    public static void sendRespawnPacket(ServerPlayer nmsPlayer, CommonPlayerSpawnInfo spawnInfo) {
        nmsPlayer.connection.send(new ClientboundRespawnPacket(
                spawnInfo,
                ClientboundRespawnPacket.KEEP_ALL_DATA
        ));


        // ** Procédure afin que le packet respawn soit valide
        PlayerRespawnNMS.sendPostRespawnPackets(nmsPlayer);
//        resyncEntitiesAfterDimensionRefresh(player);
    }

//    private void resyncEntitiesAfterDimensionRefresh(Player player) {
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                if (!player.isOnline()) return;
//
//                double range = 96.0D;
//                for (Entity entity : player.getNearbyEntities(range, range, range)) {
//                    if (entity.equals(player)) continue;
//
//                    player.hideEntity(OMCPlugin.getInstance(), entity);
//                    player.showEntity(OMCPlugin.getInstance(), entity);
//                }
//            }
//        }.runTaskLater(OMCPlugin.getInstance(), 2L);
//    }

    public static void sendRespawnPackets(ServerPlayer nmsPlayer, CommonPlayerSpawnInfo targetSpawnInfo) {
        CommonPlayerSpawnInfo currentSpawnInfo = nmsPlayer.createCommonSpawnInfo(nmsPlayer.level());
        ResourceKey<Level> pivotDimension = getPivotDimension(currentSpawnInfo.dimension());

        // changement de dimension car sinon l'ambience de la dimension n'est pas affiché
        sendRespawnPacket(nmsPlayer, getDimensionPlayerSpawnInfo(currentSpawnInfo, pivotDimension));
        sendRespawnPacket(nmsPlayer, targetSpawnInfo);
    }
    /**
     * Procédure basée sur {@link ServerPlayer#teleport} afin de corriger que le packet RESPAWN invalide
     * @param nmsPlayer le joueur (NMS)
     */
    private static void sendPostRespawnPackets(ServerPlayer nmsPlayer) {
        ServerLevel nmsWorld = nmsPlayer.level();
        LevelData levelData = nmsWorld.getLevelData();

        PlayerList playerList = ((CraftServer) Bukkit.getServer()).getServer().getPlayerList();

        nmsPlayer.connection.send(new ClientboundChangeDifficultyPacket(
                levelData.getDifficulty(), levelData.isDifficultyLocked()
        ));
        playerList.sendPlayerPermissionLevel(nmsPlayer);
        nmsPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(nmsPlayer.getAbilities()));
        playerList.sendLevelInfo(nmsPlayer, nmsWorld);
        playerList.sendAllPlayerInfo(nmsPlayer);
        playerList.sendActivePlayerEffects(nmsPlayer);

        PlayerPositionNMS.sendPos(nmsPlayer, nmsPlayer.position());

        nmsPlayer.connection.send(new ClientboundSetChunkCacheCenterPacket(
                nmsPlayer.chunkPosition().x(),
                nmsPlayer.chunkPosition().z()
        ));

        int viewDistance = nmsWorld.getServer().getPlayerList().getViewDistance();
        ChunkPos center = nmsPlayer.chunkPosition();
        for (int cx = center.x() - viewDistance; cx <= center.x() + viewDistance; cx++) {
            for (int cz = center.z() - viewDistance; cz <= center.z() + viewDistance; cz++) {
                LevelChunk chunk = nmsWorld.getChunkIfLoaded(cx, cz);
                if (chunk != null) {
                    nmsPlayer.connection.send(
                            new ClientboundLevelChunkWithLightPacket(chunk, nmsWorld.getLightEngine(), null, null, false)
                    );
                }
            }
        }
    }

    private static CommonPlayerSpawnInfo getDimensionPlayerSpawnInfo(CommonPlayerSpawnInfo base, ResourceKey<Level> dimensionKey) {
        return new CommonPlayerSpawnInfo(
                base.dimensionType(),
                dimensionKey,
                base.seed(),
                base.gameType(),
                base.previousGameType(),
                base.isDebug(),
                base.isFlat(),
                base.lastDeathLocation(),
                base.portalCooldown(),
                base.seaLevel()
        );
    }

    private static ResourceKey<Level> getPivotDimension(ResourceKey<Level> currentDimension) {
        return currentDimension.equals(Level.OVERWORLD) ? Level.END : Level.OVERWORLD;
    }


}
