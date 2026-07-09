package fr.openmc.core.utils.nms.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class EntityEquipmentNMS {

    public static void sendHelmetPacket(Player receiver, int entityId, ItemStack itemStackNMS) {
        ServerPlayer nmsReceiver = ((CraftPlayer) receiver).getHandle();

        ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(
                entityId,
                List.of(Pair.of(EquipmentSlot.HEAD, itemStackNMS))
        );

        nmsReceiver.connection.send(packet);
    }
}
