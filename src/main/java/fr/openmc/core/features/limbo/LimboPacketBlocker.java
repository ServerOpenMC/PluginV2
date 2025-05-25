package fr.openmc.core.features.limbo;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.openmc.core.OMCPlugin;
import org.bukkit.entity.Player;

public class LimboPacketBlocker {

    public void register() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Client.CHAT_COMMAND) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (!LimboManager.isInLimbo(player.getUniqueId())) return;

                String command = event.getPacket().getStrings().read(0);
                if (!command.equalsIgnoreCase("/limbo")) {
                    event.setCancelled(true);
                }
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Client.CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (!LimboManager.isInLimbo(player.getUniqueId())) return;
                event.setCancelled(true);
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Client.TAB_COMPLETE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (!LimboManager.isInLimbo(player.getUniqueId())) return;
                event.setCancelled(true);
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Client.CUSTOM_PAYLOAD) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (!LimboManager.isInLimbo(player.getUniqueId())) return;
                event.setCancelled(true);
            }
        });
    }
}
