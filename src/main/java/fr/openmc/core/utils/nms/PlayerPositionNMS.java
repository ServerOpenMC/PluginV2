package fr.openmc.core.utils.nms;

import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Classe receuillant les NMS lié au packet {@link ClientboundPlayerPositionPacket}
 * Afin de simplifier l'utilisation des NMS
 */
public class PlayerPositionNMS {
    public static void sendPos(ServerPlayer nmsPlayer, Vec3 location) {
        PositionMoveRotation positionMoveRotation = new PositionMoveRotation(
                location,
                Vec3.ZERO,
                nmsPlayer.getYRot(),
                nmsPlayer.getXRot()
        );

        // je prefere utiliser la méthode au lieu de {@link ClientboundPlayerPositionPacket}
        // car elle néccéssité un ID, qui je suppose ne doit pas etre dupli
        nmsPlayer.connection.teleport(positionMoveRotation, Set.of());
    }

    public static void sendPos(Player player, Location location) {
        sendPos((ServerPlayer) player, new Vec3(location.getX(), location.getY(), location.getZ()));
    }
}
