package fr.openmc.core.features.trashbin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Trash {

    @Command("trash")
    @CommandPermission("omc.commands.trash")
    @Description("Ouvre une poubelle pour jette les items")

    private void trash(Player player) {

        Inventory bin = Bukkit.createInventory(player, 54, ChatColor.RED + "Poubelle");

        player.openInventory(bin);
    }

}