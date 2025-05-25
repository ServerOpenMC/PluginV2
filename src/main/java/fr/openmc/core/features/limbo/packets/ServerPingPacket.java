package fr.openmc.core.features.limbo.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.limbo.LimboManager;
import net.minecraft.network.protocol.status.ServerStatus;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Optional;

public class ServerPingPacket {
    public ServerPingPacket(ProtocolManager protocolManager) {
        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL, PacketType.Status.Server.SERVER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                try {
                    ServerStatus originalStatus = packet.getSpecificModifier(ServerStatus.class).read(0);

                    net.minecraft.network.chat.Component motd = originalStatus.description();

                    Optional<ServerStatus.Version> version = originalStatus.version();

                    int realPlayers = (int) Bukkit.getOnlinePlayers().stream()
                            .filter(p -> !LimboManager.isInLimbo(p.getUniqueId()))
                            .count();

                    ServerStatus.Players players = new ServerStatus.Players(
                            Bukkit.getMaxPlayers(),
                            realPlayers,
                            List.of()
                    );

                    ServerStatus newStatus = new ServerStatus(
                            motd,
                            Optional.of(players),
                            version,
                            originalStatus.favicon(),
                            originalStatus.enforcesSecureChat()
                    );

                    packet.getSpecificModifier(ServerStatus.class).write(0, newStatus);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
