package fr.openmc.core.utils;

import fr.openmc.core.OMCPlugin;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class PacketUtils {

    public static ClientboundSetPassengersPacket createSetPassengersPacket(Entity entity, int passengerId) {
        ClientboundSetPassengersPacket setPassengersPacket = new ClientboundSetPassengersPacket(entity);
        try {
            Field passengersField = ClientboundSetPassengersPacket.class.getDeclaredField("passengers");
            passengersField.setAccessible(true);
            passengersField.set(
                    setPassengersPacket,
                    new int[]{passengerId}
            );
        } catch (Exception e) {
            OMCPlugin.getInstance().getLogger().warning(e.getMessage());
        }
        return setPassengersPacket;
    }
}
