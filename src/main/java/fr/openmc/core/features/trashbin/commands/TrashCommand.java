package fr.openmc.core.features.trashbin.commands;

import fr.openmc.core.features.trashbin.menu.TrashMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TrashCommand {

    @Command("trash")
    @CommandPermission("omc.commands.trash")
    @Description("Ouvre une poubelle pour jetter les items")
    private void trash(Player player) {

        new TrashMenu(player).open();


    }

}
