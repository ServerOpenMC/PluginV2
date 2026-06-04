package fr.openmc.core.utils.nms.entity;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class EntitySpawnNMS {

    public static void sendSpawnPacket(Player receiver, EntityType<?> entityType, int entityId, UUID entityUUID, Location loc) {
        ServerPlayer nmsReceiver = ((CraftPlayer) receiver).getHandle();

        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(
                entityId,
                entityUUID,
                loc.getX(), loc.getY(), loc.getZ(),
                loc.getPitch(), loc.getYaw(),
                entityType,
                0,
                Vec3.ZERO,
                loc.getYaw()
        );

        nmsReceiver.connection.send(packet);
    }

    public static void sendMetaDataEntity(Player receiver, Entity entity) {
        List<SynchedEntityData.DataValue<?>> dataValues = entity.getEntityData().getNonDefaultValues();
        if (dataValues == null || dataValues.isEmpty()) return;
        ClientboundSetEntityDataPacket entityDataPacket = new ClientboundSetEntityDataPacket(entity.getId(), dataValues);
        ((CraftPlayer) receiver).getHandle().connection.send(entityDataPacket);
    }

}
