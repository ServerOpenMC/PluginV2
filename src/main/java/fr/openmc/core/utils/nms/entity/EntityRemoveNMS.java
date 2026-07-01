package fr.openmc.core.utils.nms.entity;

import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class EntityRemoveNMS {

    public static void sendRemovePacket(Player receiver, int entityId) {
        ServerPlayer nmsReceiver = ((CraftPlayer) receiver).getHandle();

        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entityId);
        nmsReceiver.connection.send(packet);
    }
}
