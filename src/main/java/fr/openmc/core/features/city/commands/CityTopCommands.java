package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.CityTopMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;

public class CityTopCommands {
    @Command({"city top", "citytop"})
    @CommandPermission("omc.commands.city.top")
    @Description("Ouvre les classements inter saison des villes")
    void notationTest(Player sender) {
        List<City> cities = new ArrayList<>(CityManager.getCities());
        if (cities.isEmpty()) {
            MessagesManager.sendMessage(sender, Component.text("Aucune ville n'est créée pour le moment."), Prefix.CITY, MessageType.ERROR, true);
            return;
        }
        new CityTopMenu(sender).open();
    }

}
