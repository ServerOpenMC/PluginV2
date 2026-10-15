package fr.openmc.core.utils.nms.entity;

import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.PositionPath;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class EntityTeleportNMS {

    public static void sendTeleportPacket(Player receiver, int entityId, Location loc) {
        ServerPlayer nmsReceiver = ((CraftPlayer) receiver).getHandle();

        ClientboundEntityPositionSyncPacket packet = new ClientboundEntityPositionSyncPacket(
                entityId,
                PositionPath.of(new Vec3(loc.getX(), loc.getY(), loc.getZ())),
                loc.getYaw(),
                loc.getPitch(),
                false
        );

        nmsReceiver.connection.send(packet);
    }
}
