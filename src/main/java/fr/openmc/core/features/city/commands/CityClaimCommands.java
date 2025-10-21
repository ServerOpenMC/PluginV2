package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.actions.CityClaimAction;
import fr.openmc.core.features.city.actions.CityUnclaimAction;
import fr.openmc.core.features.city.conditions.CityClaimCondition;
import fr.openmc.core.features.city.conditions.CityUnclaimCondition;
import fr.openmc.core.features.city.menu.CityChunkMenu;
import fr.openmc.core.features.city.view.CityViewManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;


public class CityClaimCommands {
    @Command("city claim")
    @CommandPermission("omc.commands.city.claim")
    @Description("Claim un chunk pour votre ville")
    @CommandPlaceholder()
    void claim(Player sender) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityClaimCondition.canCityClaim(city, sender)) return;

        Chunk chunk = sender.getLocation().getChunk();

        CityClaimAction.startClaim(sender, chunk.getX(), chunk.getZ());
    }

    @Command("city unclaim")
    @CommandPermission("omc.commands.city.unclaim")
    @Description("Unclaim un chunk pour votre ville")
    void unclaim(Player sender) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityUnclaimCondition.canCityUnclaim(city, sender)) return;

        Chunk chunk = sender.getLocation().getChunk();

        CityUnclaimAction.startUnclaim(sender, chunk.getX(), chunk.getZ());
    }

    @Command("city claim view")
    @Description("Voir les villes aux alentours")
    @CommandPermission("omc.commands.city.view")
    void view(Player player) {
        CityViewManager.startView(player);
    }

    @Command("city map")
    @CommandPermission("omc.commands.city.map")
    @Description("Affiche la map des claims.")
    void map(Player sender) {
        new CityChunkMenu(sender).open();
    }
}
