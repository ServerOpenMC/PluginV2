package fr.openmc.core.features.leaderboards.Utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketUtils {

    public static PacketContainer getTextDisplaySpawnPacket(Location location, int entityId) {

        // Spawn packet
        PacketContainer spawn = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        spawn.getIntegers().write(0, entityId); // Entity ID
        spawn.getUUIDs().write(0, UUID.randomUUID()); // Entity UUID
        spawn.getEntityTypeModifier().write(0, EntityType.TEXT_DISPLAY); // Entity type
        spawn.getDoubles().write(0, location.getX());
        spawn.getDoubles().write(1, location.getY());
        spawn.getDoubles().write(2, location.getZ());
        spawn.getIntegers().write(1, 0); // Pitch (0)
        spawn.getIntegers().write(2, 0); // Yaw (0)

        return spawn;
    }

    public static PacketContainer getTextDisplayMetadataPacket(
            int entityId,
            String text,
            int lineWidth,
            int backgroundColor,
            int InterpolationDuration,
            byte billboardConstraints,
            Display.Brightness brightness,
            float viewRange,
            int alignment,
            boolean isSeeThrough) {
        PacketContainer metadata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metadata.getIntegers().write(0, entityId);

        List<WrappedDataValue> metadataList = new ArrayList<>();

        // Index 9: Interpolation duration
        metadataList.add(new WrappedDataValue(9, WrappedDataWatcher.Registry.get((Type) Integer.class), InterpolationDuration));

        // Index 15: Billboard constraints
        metadataList.add(new WrappedDataValue(15, WrappedDataWatcher.Registry.get((Type) Byte.class), billboardConstraints)); // 0 = FIXED, 1 = VERTICAL, 2 = HORIZONTAL, 3 = CENTER

        // Index 16: Brightness override
        int brightnessValue = (brightness.getBlockLight() << 4) | (brightness.getSkyLight() << 20);
        metadataList.add(new WrappedDataValue(16, WrappedDataWatcher.Registry.get((Type) Integer.class), brightnessValue));

        // Index 17: View range
        metadataList.add(new WrappedDataValue(17, WrappedDataWatcher.Registry.get((Type) Float.class), viewRange)); // 1.0f par défaut

        // Index 23: Text
        metadataList.add(new WrappedDataValue(23, WrappedDataWatcher.Registry.getChatComponentSerializer(false), WrappedChatComponent.fromText(text).getHandle()));

        // Index 24: Line width
        metadataList.add(new WrappedDataValue(24, WrappedDataWatcher.Registry.get((Type) Integer.class), lineWidth));

        // Index 25: Background color
        metadataList.add(new WrappedDataValue(25, WrappedDataWatcher.Registry.get((Type) Integer.class), backgroundColor)); // 0x40000000 par défaut

        // Index 27: Bitmask
        byte bitmask = 0;
        if (isSeeThrough) {
            bitmask |= 0x02;
        }
        if (alignment == 1 || alignment == 3) {
            bitmask |= 0x08;  // Aligné à gauche
        } else if (alignment == 2) {
            bitmask |= 0x10;  // Aligné à droite
        } // Sinon c'est le milieu et il n'y a rien à faire de plus
        metadataList.add(new WrappedDataValue(27, WrappedDataWatcher.Registry.get((Type) Byte.class), bitmask));

        metadata.getDataValueCollectionModifier().write(0, metadataList);

        return metadata;
    }

}
