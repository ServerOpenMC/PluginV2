package fr.openmc.core.features.city.sub.war.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.war.War;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class WarKillListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) return;

        UUID victimUUID = victim.getUniqueId();
        UUID killerUUID = killer.getUniqueId();

        City victimCity = CityManager.getPlayerCity(victimUUID);
        City killerCity = CityManager.getPlayerCity(killerUUID);

        if (victimCity == null || killerCity == null) return;

        War war = victimCity.getWar();
        if (war == null || war.getPhase() != War.WarPhase.COMBAT) return;

        if (!war.isParticipant(killerUUID)) return;

        if (war.isAttacker(killerUUID)) {
            war.incrementAttackerKills();
        } else if (war.isDefender(killerUUID)) {
            war.incrementDefenderKills();
        }
    }
}
