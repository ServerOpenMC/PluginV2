package fr.openmc.core.features.limbo.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.limbo.LimboManager;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class TabListPacket {

    public TabListPacket(ProtocolManager protocolManager) {
        // Supprimer les joueurs qui sont dans le Limbo dans le TAB
        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                EnumSet<?> actions = packet.getSpecificModifier(EnumSet.class).read(0);
                if (actions.isEmpty()) return;

                Class<? extends Enum> enumClass = actions.iterator().next().getClass();

                boolean shouldFilter = actions.contains(Enum.valueOf(enumClass, "ADD_PLAYER"))
                        || actions.contains(Enum.valueOf(enumClass, "UPDATE_LATENCY"))
                        || actions.contains(Enum.valueOf(enumClass, "UPDATE_LISTED"));
                if (!shouldFilter) return;

                List<Object> entries = packet.getSpecificModifier(List.class).read(0);

                List<Object> filtered = entries.stream()
                        .filter(entry -> {
                            try {
                                UUID profileId = (UUID) entry.getClass().getMethod("profileId").invoke(entry);
                                return !LimboManager.isInLimbo(profileId);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return true;
                            }
                        })
                        .toList();

                packet.getModifier().withType(List.class).write(0, filtered);
            }
        });
    }
}
