package fr.openmc.core.features.city.sub.notation.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.notation.menu.NotationEditionDialog;
import fr.openmc.core.utils.DateUtils;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.sql.SQLException;

public class NotationCommands {
    @Command({"city notation"})
    @CommandPermission("omc.commands.city.notation")
    @Description("Ouvre le menu des maires")
    void notationTest(Player sender) throws SQLException {
        City city = CityManager.getCity(CityManager.getAllCityUUIDs().getFirst());

        NotationEditionDialog.send(sender, DateUtils.getWeekFormat(), city);
    }

}
