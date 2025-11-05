package fr.openmc.core.features.trashbin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Trash {

    private void trashBin(Player player) {

        Inventory bin = Bukkit.createInventory(player, 54, ChatColor.RED + "Poubelle");

        player.openInventory(bin);
    }

}