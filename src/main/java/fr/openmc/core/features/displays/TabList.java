package fr.openmc.core.features.displays;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class TabList {
    private static ProtocolManager protocolManager = null;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null)
            protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                if (!DreamUtils.isInDreamWorld(event.getPlayer())) return;

                EnumSet<?> actions = packet.getSpecificModifier(EnumSet.class).read(0);
                if (actions.isEmpty()) return;

                Class<? extends Enum> enumClass = actions.iterator().next().getClass();

                boolean shouldFilter = actions.contains(Enum.valueOf(enumClass, "ADD_PLAYER"))
                        || actions.contains(Enum.valueOf(enumClass, "UPDATE_LATENCY"))
                        || actions.contains(Enum.valueOf(enumClass, "UPDATE_LISTED"));
                if (!shouldFilter) return;

                List<Object> entries = packet.getSpecificModifier(List.class).read(0);

                UUID viewerId = event.getPlayer().getUniqueId();

                List<Object> filtered = entries.stream()
                        .filter(entry -> {
                            try {
                                UUID profileId = (UUID) entry.getClass().getMethod("profileId").invoke(entry);
                                return profileId.equals(viewerId);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        })
                        .toList();

                packet.getModifier().withType(List.class).write(0, filtered);
            }
        });
    }

    public static void updateHeaderFooter(Player player, String header, String footer) {
        try {
            if (protocolManager == null) return;
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
            packet.getChatComponents().write(0, WrappedChatComponent.fromText(header))
                    .write(1, WrappedChatComponent.fromText(footer));
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateTabList(Player player) {
        int visibleOnlinePlayers = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player.canSee(p)) {
                visibleOnlinePlayers++;
            }
        }

        boolean isInDream = DreamUtils.isInDream(player);
        String logo;
        if (isInDream) {
            logo = FontImageWrapper.replaceFontImages(":dream_openmc:");
        } else {
            logo = FontImageWrapper.replaceFontImages(":openmc:");
        }

        String header = !isInDream ? "\n\n\n\n\n\n\n" + logo + "\n\n  §eJoueurs en ligne §7: §6" + visibleOnlinePlayers + "§7/§e" + Bukkit.getMaxPlayers() + "  \n" : "\n\n\n\n\n\n\n" + logo + "\n\n";
        String footer = isInDream ? "\n§1play.openmc.fr\n" : "\n§dplay.openmc.fr\n";

        updateHeaderFooter(player, header, footer);
    }

}
