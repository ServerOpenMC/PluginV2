package fr.openmc.core.features.city.sub.mayor.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener  {
    private final CityManager cityManager;
    private final MayorManager mayorManager;

    public JoinListener(CityManager cityManager, MayorManager mayorManager) {
        this.cityManager = cityManager;
        this.mayorManager = mayorManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        City playerCity = cityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) return;

        if (playerCity.getLaw() == null) {
            mayorManager.createCityLaws(playerCity, false, null);
        }

        if (!FeaturesRewards.hasUnlockFeature(playerCity, FeaturesRewards.Feature.MAYOR)) return;

        if (mayorManager.phaseMayor == 2 && mayorManager.cityMayor.get(playerCity.getUniqueId()) == null) {
            if (playerCity.getMembers().size() >= MayorManager.MEMBER_REQUEST_ELECTION) {
                mayorManager.createMayor(null, null, playerCity, null, null, null, null, ElectionType.ELECTION);
            }
            mayorManager.createMayor(null, null, playerCity, null, null, null, null, ElectionType.OWNER_CHOOSE);

            mayorManager.runSetupMayor(playerCity);
        } else if (mayorManager.phaseMayor == 1 && mayorManager.cityMayor.get(playerCity.getUniqueId()) == null) {
            if (playerCity.getMembers().size()>=MayorManager.MEMBER_REQUEST_ELECTION) {
                mayorManager.createMayor(null,null, playerCity, null, null, null, null, ElectionType.ELECTION);
            }
            mayorManager.createMayor(null, null, playerCity, null, null, null, null, ElectionType.OWNER_CHOOSE);

        }
    }
}