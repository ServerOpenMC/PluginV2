package fr.openmc.core.registry.ambient;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import fr.openmc.api.datapacks.DatapackInjector;
import fr.openmc.api.datapacks.injectors.DimensionTypesInjector;
import fr.openmc.core.OMCPlugin;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class CustomAmbient {
    public abstract String getId();
    public abstract DimensionTypesInjector.DimensionTypeBuilder getDimensionType();

    public DatapackInjector toDimensionTypeInjector() {
        return new DimensionTypesInjector("omc_ambient").add(getId(), getDimensionType());
    }
    public void apply(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        ServerLevel nmsWorld = nmsPlayer.level();

        forceClientRespawnReload(nmsPlayer, getCommonPlayerSpawnInfo(nmsPlayer));
        sendPostRespawnPackets(player, nmsPlayer, nmsWorld);
        resyncEntitiesAfterDimensionRefresh(player);
    }

    public void reset(Player player) {
        sendRealDimension(player);
    }

    private CommonPlayerSpawnInfo getCommonPlayerSpawnInfo(ServerPlayer nmsPlayer) {
        ServerLevel nmsWorld = (ServerLevel) nmsPlayer.level();
        CommonPlayerSpawnInfo spawnInfo = nmsPlayer.createCommonSpawnInfo(nmsPlayer.level());

        ResourceKey<DimensionType> key = ResourceKey.create(
                Registries.DIMENSION_TYPE,
                Identifier.fromNamespaceAndPath("openmc", "test")
        );

        Registry<DimensionType> dimRegistry =
                nmsWorld.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE);

        Holder<DimensionType> dimensionTypeHolder = dimRegistry.get(key).orElseThrow(() ->
                new IllegalStateException("DimensionType openmc:test introuvable")
        );

        return new CommonPlayerSpawnInfo(
                dimensionTypeHolder,
                spawnInfo.dimension(),
                spawnInfo.seed(),
                spawnInfo.gameType(),
                spawnInfo.previousGameType(),
                spawnInfo.isDebug(),
                spawnInfo.isFlat(),
                spawnInfo.lastDeathLocation(),
                spawnInfo.portalCooldown(),
                spawnInfo.seaLevel()
        );
    }

    private CommonPlayerSpawnInfo withDimension(CommonPlayerSpawnInfo base, ResourceKey<Level> dimensionKey) {
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

    private static void sendRespawnPacket(ServerPlayer nmsPlayer, CommonPlayerSpawnInfo spawnInfo) {
        nmsPlayer.connection.send(new ClientboundRespawnPacket(
                spawnInfo,
                ClientboundRespawnPacket.KEEP_ALL_DATA
        ));
    }

    /**
     * Procédure qu'a paper durant une teleportation entre 2 dimensions
     */
    private void sendPostRespawnPackets(Player player, ServerPlayer nmsPlayer, ServerLevel nmsWorld) {
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
        sendPos(player, player.getLocation());

        nmsPlayer.connection.send(new ClientboundSetChunkCacheCenterPacket(
                nmsPlayer.chunkPosition().x(),
                nmsPlayer.chunkPosition().z()
        ));
        int viewDistance = nmsWorld.getServer().getPlayerList().getViewDistance();
        net.minecraft.world.level.ChunkPos center = nmsPlayer.chunkPosition();

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

    private ResourceKey<Level> getPivotDimension(ResourceKey<Level> currentDimension) {
        return currentDimension.equals(Level.OVERWORLD) ? Level.END : Level.OVERWORLD;
    }

    private void forceClientRespawnReload(ServerPlayer nmsPlayer, CommonPlayerSpawnInfo targetSpawnInfo) {
        CommonPlayerSpawnInfo currentSpawnInfo = nmsPlayer.createCommonSpawnInfo(nmsPlayer.level());
        ResourceKey<Level> pivotDimension = getPivotDimension(currentSpawnInfo.dimension());

        // changement de dimension car sinon l'ambience de la dimension n'est pas affiché
        sendRespawnPacket(nmsPlayer, withDimension(currentSpawnInfo, pivotDimension));
        sendRespawnPacket(nmsPlayer, targetSpawnInfo);
    }

    private void resyncEntitiesAfterDimensionRefresh(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }

                double range = 96.0D;
                for (Entity entity : player.getNearbyEntities(range, range, range)) {
                    if (entity.equals(player)) {
                        continue;
                    }
                    player.hideEntity(OMCPlugin.getInstance(), entity);
                    player.showEntity(OMCPlugin.getInstance(), entity);
                }
            }
        }.runTaskLater(OMCPlugin.getInstance(), 2L);
    }

    public void sendPos(Player player, Location location) {
        PacketContainer positionPacket = new PacketContainer(PacketType.Play.Server.POSITION);
        Vec3 position = new Vec3(location.getX(), location.getY(), location.getZ());
        PositionMoveRotation positionMoveRotation = new PositionMoveRotation(position, Vec3.ZERO,
                location.getYaw(), location.getPitch());

        positionPacket.getStructures()
                .withType(PositionMoveRotation.class)
                .write(0, positionMoveRotation);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, positionPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRealDimension(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        ServerLevel nmsWorld = (ServerLevel) nmsPlayer.level();

        forceClientRespawnReload(nmsPlayer, nmsPlayer.createCommonSpawnInfo(nmsPlayer.level()));
        sendPostRespawnPackets(player, nmsPlayer, nmsWorld);
        resyncEntitiesAfterDimensionRefresh(player);
    }
}
