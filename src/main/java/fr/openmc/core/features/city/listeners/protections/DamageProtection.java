package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import fr.openmc.core.features.city.sub.war.War;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageProtection implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();

        Player attacker = null;
        if (damager instanceof Player p) {
            attacker = p;
        } else if (damager instanceof Projectile proj && proj.getShooter() instanceof Player shooter) {
            attacker = shooter;
        }

        if (victim instanceof Player victimPlayer && attacker != null) {
            City victimCity = CityManager.getPlayerCity(victimPlayer.getUniqueId());
            City attackerCity = CityManager.getPlayerCity(attacker.getUniqueId());

            War victimWar = victimCity == null ? null : victimCity.getWar();
            War attackerWar = attackerCity == null ? null : attackerCity.getWar();
            if (victimWar != null || attackerWar != null) {
                boolean opposingParticipants = victimWar != null
                        && victimWar == attackerWar
                        && victimWar.getPhase() == War.WarPhase.COMBAT
                        && victimWar.isParticipant(victimPlayer.getUniqueId())
                        && victimWar.isParticipant(attacker.getUniqueId())
                        && (victimWar.isAttacker(victimPlayer.getUniqueId())
                            && victimWar.isDefender(attacker.getUniqueId())
                            || victimWar.isDefender(victimPlayer.getUniqueId())
                            && victimWar.isAttacker(attacker.getUniqueId()));
                if (!opposingParticipants) {
                    event.setCancelled(true);
                    return;
                }
                return;
            }

            if (victimCity != null && victimCity == attackerCity) {
                if (!victimCity.getLaw().isPvp())
                    event.setCancelled(true);
                return;
            }
        }

        if (victim instanceof Player victimPlayer)
            ProtectionsManager.verify(victimPlayer, event, victimPlayer.getLocation());

        if (MascotUtils.canBeAMascot(victim)) return;

        if (attacker != null) {
            ProtectionsManager.verify(attacker, event, victim.getLocation());
        }
    }
}
