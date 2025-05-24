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
        MayorManager mayorManager = MayorManager.getInstance();
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) return;

        if (playerCity.getLaw() == null) {
            MayorManager.createCityLaws(playerCity, false, null);
        }

        if (mayorManager.phaseMayor == 2 && mayorManager.cityMayor.get(playerCity)==null) {
            mayorManager.runSetupMayor(playerCity);
        } else if (mayorManager.phaseMayor == 1 && mayorManager.cityMayor.get(playerCity)==null) {
            if (playerCity.getMembers().size()>=mayorManager.MEMBER_REQ_ELECTION) {
                mayorManager.createMayor(null,null, playerCity, null, null, null, null, ElectionType.ELECTION);
            }
            mayorManager.createMayor(null, null, playerCity, null, null, null, null, ElectionType.OWNER_CHOOSE);

        }
    }
}
