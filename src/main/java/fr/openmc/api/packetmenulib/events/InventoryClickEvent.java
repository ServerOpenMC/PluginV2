package fr.openmc.api.packetmenulib.events;

import fr.openmc.api.packetmenulib.menu.ClickType;
import org.bukkit.entity.Player;

public record InventoryClickEvent(ClickType clickType, int slot, Player player) {
}
