package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.view.CityViewManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class CityClaimViewCommand {
    @Command({"city claim view"})
    @Description("Voir les villes aux alentours")
    @CommandPermission("omc.commands.city.claim.view")
    void claimViewCommand(Player player) {
        CityViewManager.startView(player);
    }
}
