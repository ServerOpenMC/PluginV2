package fr.openmc.core.features.city.mayor.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.ElectionType;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
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

        if (MayorManager.phaseMayor == 2 && MayorManager.cityMayor.get(playerCity)==null) {
            MayorManager.runSetupMayor(playerCity);
        } else if (MayorManager.phaseMayor == 1 && MayorManager.cityMayor.get(playerCity)==null) {
            if (playerCity.getMembers().size()>=MayorManager.MEMBER_REQ_ELECTION) {
                MayorManager.createMayor(null,null, playerCity, null, null, null, null, ElectionType.ELECTION);
            }
            MayorManager.createMayor(null, null, playerCity, null, null, null, null, ElectionType.OWNER_CHOOSE);

        }
    }
}
