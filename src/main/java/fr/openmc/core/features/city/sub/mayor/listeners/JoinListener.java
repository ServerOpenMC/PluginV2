package fr.openmc.core.features.city.sub.mayor.listeners;

import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.models.MayorPhase;
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
        City playerCity = City.ofPlayer(player);

        if (playerCity == null) return;

        if (playerCity.getLaw() == null)
            mayorManager.createCityLaws(playerCity, false, null);

        if (!FeaturesRewards.hasUnlockFeature(playerCity, FeaturesRewards.Feature.MAYOR)) return;

        if (playerCity.getMayorPhase().equals(MayorPhase.MAYOR_ELECTED) && mayorManager.getCityMayor().get(playerCity.getUniqueId()) == null) {
            if (playerCity.getMembers().size() >= MayorManager.MEMBER_REQUEST_ELECTION) {
                mayorManager.createMayor(null, null, playerCity, null, null, null, null, ElectionType.ELECTION);
            }
            mayorManager.createMayor(null, null, playerCity, null, null, null, null, ElectionType.OWNER_CHOOSE);

            mayorManager.runSetupMayor(playerCity);
        } else if (playerCity.getMayorPhase().equals(MayorPhase.OPEN_ELECTION) && mayorManager.getCityMayor().get(playerCity.getUniqueId()) == null) {
            if (playerCity.getMembers().size()>=MayorManager.MEMBER_REQUEST_ELECTION) {
                mayorManager.createMayor(null,null, playerCity, null, null, null, null, ElectionType.ELECTION);
            }
            mayorManager.createMayor(null, null, playerCity, null, null, null, null, ElectionType.OWNER_CHOOSE);

        }
    }
}