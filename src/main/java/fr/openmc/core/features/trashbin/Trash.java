package fr.openmc.core.features.trashbin;

import fr.openmc.api.packetmenulib.menu.InventoryType;
import org.bukkit.entity.Player;

public class Trash {

    private void trashBin(Player player) {

        Inventory bin = Bukkit.createInventory(player, InventoryType.GENERIC_9X6,ChatColor.RED + "Poubelle");

        player.openInventory(bin);
    }

}