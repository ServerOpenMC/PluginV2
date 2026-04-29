package fr.openmc.core.features.city.sub.milestone.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.milestone.menu.CityMilestoneMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class CityMilestoneCommands {
    @Command({"city milestone", "ville milestone"})
    @CommandPermission("omc.commands.city.milestone")
    @Description("Ouvre le menu des maires")
    void milestone(Player sender) {
        City playerCity = CityManager.getPlayerCity(sender.getUniqueId());

        if (playerCity == null) return;

        new CityMilestoneMenu(sender, playerCity).open();
    }

}
