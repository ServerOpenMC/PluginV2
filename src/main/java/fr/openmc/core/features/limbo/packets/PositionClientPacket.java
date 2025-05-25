package fr.openmc.core.features.limbo.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.limbo.LimboManager;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PositionClientPacket {
    public PositionClientPacket(ProtocolManager protocolManager) {
        protocolManager.addPacketListener(new PacketAdapter(
                OMCPlugin.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Client.POSITION
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                UUID uuid = player.getUniqueId();

                if (!LimboManager.isInLimbo(uuid)) return;

                PacketContainer packet = event.getPacket();
                double x = packet.getDoubles().read(0);
                double y = packet.getDoubles().read(1);
                double z = packet.getDoubles().read(2);


                LimboManager.getLimboPlayer(uuid).updateVirtualLocation(x, y, z);
            }
        });
    }
}
