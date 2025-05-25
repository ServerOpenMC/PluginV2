package fr.openmc.core.features.limbo.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

public class RespawnPacket {
    private static ProtocolManager protocolManager;

    public RespawnPacket(ProtocolManager manager) {
        protocolManager = manager;
    }

    private static CommonPlayerSpawnInfo getCommonPlayerSpawnInfo(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        MinecraftServer server = nmsPlayer.server;
        ServerLevel level = server.getLevel(Level.END);

        Holder<DimensionType> dimTypeHolder = level.dimensionTypeRegistration();
        ResourceKey<Level> dimensionKey = Level.END;
        long seed = 0;
        GameType gm = GameType.CREATIVE;
        GameType prevGm = null;
        boolean debug = false;
        boolean flat = true;

        GlobalPos lastDeathLocation = new GlobalPos(dimensionKey, new BlockPos((int) player.getX(), (int) player.getY(), (int) player.getZ()));
        int portalCooldown = 0;
        int seaLevel = level.getSeaLevel();

        return new CommonPlayerSpawnInfo(
                dimTypeHolder,
                dimensionKey,
                seed,
                gm,
                prevGm,
                debug,
                flat,
                Optional.of(lastDeathLocation),
                portalCooldown,
                seaLevel
        );
    }

    public static void send(Player player) {
        PacketContainer respawn = new PacketContainer(PacketType.Play.Server.RESPAWN);
        respawn.getStructures().withType(CommonPlayerSpawnInfo.class).write(0, getCommonPlayerSpawnInfo(player));

        try {
            protocolManager.sendServerPacket(player, respawn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
