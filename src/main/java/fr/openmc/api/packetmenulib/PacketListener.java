package fr.openmc.api.packetmenulib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import fr.openmc.api.packetmenulib.events.InventoryClickEvent;
import fr.openmc.api.packetmenulib.events.InventoryCloseEvent;
import fr.openmc.api.packetmenulib.menu.ClickType;
import fr.openmc.api.packetmenulib.menu.Menu;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.UUID;

public class PacketListener extends PacketAdapter {
    @Getter
    private static PacketListener instance;

    public PacketListener(Plugin plugin) {
        super(plugin, PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.CLOSE_WINDOW, PacketType.Play.Server.OPEN_WINDOW, PacketType.Play.Server.SET_SLOT);
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
        instance = this;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            PacketContainer packet = event.getPacket();
            int windowId = packet.getIntegers().read(0);
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();

            if (PacketMenuLib.getOpenMenus().containsKey(uuid) && PacketMenuLib.getWindowIds().get(uuid) != windowId) {
                PacketMenuLib.getOpenMenus().get(uuid).onInventoryClose(new InventoryCloseEvent(player));
                PacketMenuLib.getOpenMenus().remove(uuid);
                PacketMenuLib.updateInv(Objects.requireNonNull(player));
            }
        } else if (event.getPacketType() == PacketType.Play.Server.SET_SLOT && PacketMenuLib.getOpenMenus().containsKey(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CLICK) {
            PacketContainer packet = event.getPacket();
            int windowId = packet.getIntegers().read(0);
            int stateId = packet.getIntegers().read(1);
            short slot = packet.getShorts().read(0);
            byte button = packet.getBytes().read(0);
            int mode = ( (net.minecraft.world.inventory.ClickType) packet.getStructures().withType(net.minecraft.world.inventory.ClickType.class).read(0)).ordinal();

            if (PacketMenuLib.getWindowIds().containsKey(event.getPlayer().getUniqueId()) && windowId == PacketMenuLib.getWindowIds().get(event.getPlayer().getUniqueId())) {
                Player player = event.getPlayer();
                event.setCancelled(true);
                UUID uuid = player.getUniqueId();

                if (PacketMenuLib.getOpenMenus().containsKey(uuid)) {
                    Menu menu = PacketMenuLib.getOpenMenus().get(uuid);

                    if (slot == -999 && (mode == 0 || mode == 4)) {
                        menu.onInventoryClick(new InventoryClickEvent(ClickType.CLICK_OUTSIDE, slot, player));
                    } else {
                        ClickType clickType = switch (mode) {
                            case 0 -> button == 1 ? ClickType.RIGHT_CLICK : ClickType.LEFT_CLICK;
                            case 1 -> button == 1 ? ClickType.SHIFT_RIGHT_CLICK : ClickType.SHIFT_LEFT_CLICK;
                            case 6 -> ClickType.DOUBLE_CLICK;
                            default -> ClickType.OTHER;
                        };

                        PacketMenuLib.updateMenu(menu, player, stateId);
                        menu.onInventoryClick(new InventoryClickEvent(clickType, slot, player));
                    }
                }
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();

            if (PacketMenuLib.getOpenMenus().containsKey(uuid)) {
                PacketMenuLib.getOpenMenus().get(uuid).onInventoryClose(new InventoryCloseEvent(player));
                PacketMenuLib.getOpenMenus().remove(uuid);
                PacketMenuLib.updateInv(Objects.requireNonNull(player));
            } // We don't verify if it is a good window id because if we do, the player can close the inventory without a packet and the event will never be called
        }
    }
}
