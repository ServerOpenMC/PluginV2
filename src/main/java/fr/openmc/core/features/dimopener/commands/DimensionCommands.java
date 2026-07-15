package fr.openmc.core.features.dimopener.commands;

import fr.openmc.core.features.dimopener.menu.DimensionListMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;

@Command({"dimension", "dimensions", "dimopener"})
public class DimensionCommands {

    @CommandPlaceholder()
    public void openMenu(Player player) {
        new DimensionListMenu(player).open();
    }
}