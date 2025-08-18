package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.menu.CityTopMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class CityTopCommands {
    @Command({"city top", "citytop"})
    @CommandPermission("omc.commands.city.top")
    @Description("Ouvre les classements inter saison des villes")
    void notationTest(Player sender) {
        new CityTopMenu(sender).open();
    }

}
