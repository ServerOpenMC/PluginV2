package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.CityTopMenu;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
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
	        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.commands.top.empty"), Prefix.CITY, MessageType.ERROR, true);
            return;
        }
        new CityTopMenu(sender).open();
    }

}
