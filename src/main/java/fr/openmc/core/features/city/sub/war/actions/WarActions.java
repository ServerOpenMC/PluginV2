package fr.openmc.core.features.city.sub.war.actions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WarActions {

    public static void beginLaunchWar(Player player, City cityAttack) {
        UUID launcherUUID = player.getUniqueId();
        City launchCity = CityManager.getPlayerCity(launcherUUID);


    }
}