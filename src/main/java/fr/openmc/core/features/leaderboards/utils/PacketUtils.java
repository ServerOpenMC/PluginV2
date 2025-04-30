package fr.openmc.core.features.leaderboards.utils;

import com.mojang.math.Transformation;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Brightness;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class PacketUtils {

    public static ClientboundAddEntityPacket getAddEntityPacket(int entityId, Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        return new ClientboundAddEntityPacket(entityId, UUID.randomUUID(), x, y, z, 0, 0, net.minecraft.world.entity.EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0);
    }

    public static ClientboundSetEntityDataPacket getSetEntityDataPacket(int entityId,
                                                                        String text,
                                                                        ServerLevel level,
                                                                        Vector3f scale,
                                                                        net.minecraft.world.entity.Display.BillboardConstraints billboardConstraints,
                                                                        int alignment,
                                                                        boolean isSeeThrough,
                                                                        boolean UseDefaultBackgroundColor,
                                                                        float viewRange) {
        net.minecraft.world.entity.Display.TextDisplay td = new net.minecraft.world.entity.Display.TextDisplay(net.minecraft.world.entity.EntityType.TEXT_DISPLAY, level);
        td.setId(entityId);
        HolderLookup.Provider provider = HolderLookup.Provider.create(Stream.empty());
        td.setText(Objects.requireNonNull(Component.Serializer.fromJson(text, provider)));
        td.setBrightnessOverride(Brightness.FULL_BRIGHT);
        td.setTransformation(new Transformation(new Vector3f(), new Quaternionf(), scale, new Quaternionf()));
        td.setBillboardConstraints(billboardConstraints);
        byte bitmask = 0;
        if (isSeeThrough) {
            bitmask |= 0x02;
        }
        if (UseDefaultBackgroundColor) {
            bitmask |= 0x04;
        }
        if (alignment == 1 || alignment == 3) {
            bitmask |= 0x08;  // Aligné à gauche
        } else if (alignment == 2) {
            bitmask |= 0x10;  // Aligné à droite
        } // Sinon c'est le milieu et il n'y a rien à faire de plus
        td.setFlags(bitmask);
        td.setViewRange(viewRange);
        td.getEntityData().set(new EntityDataAccessor<>(24, EntityDataSerializers.INT),Integer.MAX_VALUE);

        return new ClientboundSetEntityDataPacket(
                td.getId(), td.getEntityData().packAll()
        );
    }


}
