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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.UUID;

public class MascotsSoundListener {

    public MascotsSoundListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                OMCPlugin.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Server.NAMED_SOUND_EFFECT
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                String soundName = packet.getSoundEffects().read(0).toString();
                String[] splitedSound = soundName.split("\\.");

                if (!splitedSound[0].equals("ENTITY")) return;

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

                mascotsNear.forEach(mascot -> {
                    if (mascot.getEntity().getType().equals(EntityType.fromName(splitedSound[1].toUpperCase()))) {
                        event.setCancelled(true);
                        return;
                    }
                });
            }
        });
    }
}
