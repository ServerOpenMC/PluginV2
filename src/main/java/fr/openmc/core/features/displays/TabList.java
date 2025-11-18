package fr.openmc.core.features.displays;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TabList {
    private static ProtocolManager protocolManager = null;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null)
            protocolManager = ProtocolLibrary.getProtocolManager();
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

        String header = "\n\n\n\n\n\n\n" + FontImageWrapper.replaceFontImages(":openmc:") + "\n\n  §eJoueurs en ligne §7: §6" + visibleOnlinePlayers + "§7/§e" + Bukkit.getMaxPlayers() + "  \n";
        String footer = "\n§dplay.openmc.fr\n";

        updateHeaderFooter(player, header, footer);
    }

}
