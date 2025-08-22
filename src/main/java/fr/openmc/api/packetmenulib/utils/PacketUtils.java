package fr.openmc.api.packetmenulib.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import fr.openmc.core.OMCPlugin;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PacketUtils {

    private static final ProtocolManager manager = ProtocolLibrary.getProtocolManager();

    public static void sendOpenInventoryPacket(Player player, int containerId, MenuType<?> type, net.kyori.adventure.text.Component title) {
        try {
            PacketContainer packet = manager.createPacket(PacketType.Play.Server.OPEN_WINDOW);

            packet.getIntegers().write(0, containerId); // Window ID
            packet.getStructures().withType(MenuType.class).write(0, type);
            packet.getStructures().withType(Component.class).write(0, PaperAdventure.asVanilla(title));
            manager.sendServerPacket(player, packet);
        } catch (Exception e) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("An error occurred while sending the open inventory packet to {}: {}", player.getName(), e.getMessage(), e);
        }

    }

    public static void sendContainerContentPacket(Player player, int containerId, int stateId, List<ItemStack> items, ItemStack cursorItem) {
        try {
            PacketContainer packet = manager.createPacket(PacketType.Play.Server.WINDOW_ITEMS);

            for (int i = 0; i < items.size(); i++) {
                if (items.get(i) == null) {
                    items.set(i, new ItemStack(Material.AIR));
                }
            }

            packet.getIntegers().write(0, containerId); // Window ID
            packet.getIntegers().write(1, stateId); // State ID

            packet.getItemListModifier().write(0, items);
            packet.getItemModifier().write(0, cursorItem);

            manager.sendServerPacket(player, packet);
        } catch (Exception e) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("An error occurred while sending the container content packet to {}: {}", player.getName(), e.getMessage(), e);
        }
    }

    public static void sendCloseInventoryPacket(Player player, int containerId) {
        try {
            PacketContainer packet = manager.createPacket(PacketType.Play.Server.CLOSE_WINDOW);

            packet.getIntegers().write(0, containerId); // Window ID

            manager.sendServerPacket(player, packet);
        } catch (Exception e) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("An error occurred while sending the close inventory packet to {}: {}", player.getName(), e.getMessage(), e);
        }
    }
}
