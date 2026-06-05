package fr.openmc.core.utils.nms;

import fr.openmc.core.OMCPlugin;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Classe receuillant les NMS lié au packet {@link ClientboundRespawnPacket}
 * Afin de simplifier l'utilisation des NMS
 *
 * @see CustomAmbient utilisation trés spécifique, on affiche une dimension_type sur le joueur, ds la dimension actuelle
 */
public class PlayerRespawnNMS {

    /**
     * Envoie juste le packet respawn.
     * @param nmsPlayer le joueur en NMS
     * @param spawnInfo Les informations de la dimension ou il est envoyé
     */
    private static void sendSimplePacket(ServerPlayer nmsPlayer, CommonPlayerSpawnInfo spawnInfo) {
        nmsPlayer.connection.send(new ClientboundRespawnPacket(
                spawnInfo,
                ClientboundRespawnPacket.KEEP_ALL_DATA
        ));
    }

    /**
     * Envoie le packet RESPAWN au joueur ciblé, avec des informations données
     * En utilisant des procédures qui assurent le tout
     * @param nmsPlayer le joueur en NMS
     * @param spawnInfo Les informations de la dimension ou il est envoyé
     */
    public static void sendPacket(ServerPlayer nmsPlayer, CommonPlayerSpawnInfo spawnInfo) {
        // ** Envoie de l'entete du packet respawn
        sendSimplePacket(nmsPlayer, spawnInfo);

        // ** Procédure afin que le packet respawn soit valide
        sendPostRespawnPackets(nmsPlayer);
        resyncEntities(nmsPlayer.getBukkitEntity().getPlayer());
    }


    /**
     * Envoie le packet RESPAWN au joueur ciblé avec les informations de spawn,
     * plus un pivot qui permet d'afficher un changement de dimension
     * @param nmsPlayer le joueur ciblé
     * @param targetSpawnInfo les informations de spawn
     * @param pivotDimension la dimension de pivot
     * Note : Assurer vous que le pivot ne pointe pas vers une dimension ou le joueur est déja
     * {@code nmsPlayer.createCommonSpawnInfo(nmsPlayer.level()).dimension().equals(Level.OVERWORLD) ? Level.END : Level.OVERWORLD;}
     */
    public static void sendPacket(ServerPlayer nmsPlayer, CommonPlayerSpawnInfo targetSpawnInfo, ResourceKey<Level> pivotDimension) {
        // changement de dimension car sinon l'ambience de la dimension n'est pas affiché
        // l'unique packet de repsawn pour simuler un changement de dimension + envoie du dimension type = plus rapide
        sendSimplePacket(nmsPlayer, createPlayerSpawnInfoWithDimension(nmsPlayer, pivotDimension));
        sendPacket(nmsPlayer, targetSpawnInfo);
    }

    /**
     * Reaffiche les entités autour du joueur
     * @param player le joueur ciblé
     */
    private static void resyncEntities(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;

                double range = 96.0D;
                for (Entity entity : player.getNearbyEntities(range, range, range)) {
                    if (entity.equals(player)) continue;

                    player.hideEntity(OMCPlugin.getInstance(), entity);
                    player.showEntity(OMCPlugin.getInstance(), entity);
                }
            }
        }.runTaskLater(OMCPlugin.getInstance(), 2L);
    }

    /**
     * Procédure basée sur {@link ServerPlayer#teleport} afin de corriger que le packet RESPAWN invalide
     * @param nmsPlayer le joueur (NMS)
     */
    private static void sendPostRespawnPackets(ServerPlayer nmsPlayer) {
        ServerLevel nmsWorld = nmsPlayer.level();
        LevelData levelData = nmsWorld.getLevelData();

        // ** Lancement des différentes procédure nécessaire apres un Repsawn Packet
        nmsPlayer.connection.send(new ClientboundChangeDifficultyPacket(
                levelData.getDifficulty(), levelData.isDifficultyLocked()
        ));

        PlayerList playerList = ((CraftServer) Bukkit.getServer()).getServer().getPlayerList();
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

    /**
     * Créer les Informations de Spawn avec la dimension ciblé, en gardant les autres informations de base
     * @param nmsPlayer le joueur ciblé
     * @param dimensionKey la dimension type key
     * @return Information de Spawn modifié
     */
    private static CommonPlayerSpawnInfo createPlayerSpawnInfoWithDimension(ServerPlayer nmsPlayer, ResourceKey<Level> dimensionKey) {
        CommonPlayerSpawnInfo base = nmsPlayer.createCommonSpawnInfo(nmsPlayer.level());

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
}
