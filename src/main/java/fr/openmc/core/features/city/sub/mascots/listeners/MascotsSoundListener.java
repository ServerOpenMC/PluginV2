package fr.openmc.core.features.city.sub.mascots.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import fr.openmc.core.utils.EnumUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MascotsSoundListener {

    private static final Map<EntityType, EntityType> SOUND_TO_ENTITY = Map.of(
            EntityType.COW, EntityType.MOOSHROOM
    );

    public MascotsSoundListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                OMCPlugin.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Server.NAMED_SOUND_EFFECT
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                Object soundObj = packet.getSoundEffects().read(0); // CraftSound
                String soundName = soundObj.toString(); // CraftSound{holder=Reference{ResourceKey[minecraft:sound_event / minecraft:entity.zombie.ambient]=...}}

                int index = soundName.indexOf("/ ");
                if (index == -1) return;
                String name = soundName.substring(index + 2, soundName.indexOf("]", index)); // "minecraft:entity.zombie.ambient"

                if (!name.startsWith("minecraft:entity.")) return;

                String[] parts = name.split("\\.");
                String entityName = parts[1].toUpperCase(Locale.ROOT);
                EntityType soundEntity = EnumUtils.match(entityName, EntityType.class);
                if (soundEntity == null) return;

                UUID playerUUID = event.getPlayer().getUniqueId();
                if (PlayerSettingsManager.getPlayerSettings(playerUUID).getSetting(SettingType.MASCOT_PLAY_SOUND_POLICY))
                    return;

                double x = packet.getIntegers().read(0) / 8.0;
                double y = packet.getIntegers().read(1) / 8.0;
                double z = packet.getIntegers().read(2) / 8.0;

                World world = event.getPlayer().getWorld();

                Location location = new Location(world, x, y, z);

                List<Mascot> mascotsNear = world.getNearbyEntities(location, 16, 16, 16)
                        .stream()
                        .filter(entity -> MascotsManager.mascotsByEntityUUID.containsKey(entity.getUniqueId()))
                        .map(entity -> MascotsManager.mascotsByEntityUUID.get(entity.getUniqueId()))
                        .toList();

                for (Mascot mascot : mascotsNear) {
                    Entity entity = mascot.getEntity();
                    EntityType entityType = entity.getType();

                    if (entityType.equals(soundEntity)
                            || (SOUND_TO_ENTITY.containsKey(soundEntity) && SOUND_TO_ENTITY.get(soundEntity) == entityType)) {

                        if (entity.getLocation().distanceSquared(location) < 1.0) {
                            event.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        });
    }
}
