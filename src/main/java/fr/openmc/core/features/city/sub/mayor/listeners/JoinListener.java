package fr.openmc.core.features.city.sub.mayor.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener  {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) return;

        if (playerCity.getLaw() == null) {
            MayorManager.createCityLaws(playerCity, false, null);
        }

        if (MayorManager.phaseMayor == 2 && MayorManager.cityMayor.get(playerCity.getUUID()) == null) {
            if (playerCity.getMembers().size() >= MayorManager.MEMBER_REQUEST_ELECTION) {
                MayorManager.createMayor(null, null, playerCity, null, null, null, null, ElectionType.ELECTION);
            }
            MayorManager.createMayor(null, null, playerCity, null, null, null, null, ElectionType.OWNER_CHOOSE);

            MayorManager.runSetupMayor(playerCity);
        } else if (MayorManager.phaseMayor == 1 && MayorManager.cityMayor.get(playerCity.getUUID()) == null) {
            if (playerCity.getMembers().size()>=MayorManager.MEMBER_REQUEST_ELECTION) {
                MayorManager.createMayor(null,null, playerCity, null, null, null, null, ElectionType.ELECTION);
            }
            MayorManager.createMayor(null, null, playerCity, null, null, null, null, ElectionType.OWNER_CHOOSE);

        }
    }
}