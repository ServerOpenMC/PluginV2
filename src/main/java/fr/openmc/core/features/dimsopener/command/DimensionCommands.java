package fr.openmc.core.features.dimsopener.command;

import fr.openmc.core.features.dimsopener.menus.DimensionListMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;

@Command("dimension")
public class DimensionCommands {

    @DefaultFor("~")
    public void dimension(Player player) {
        new DimensionListMenu(player).open();
    }

}
