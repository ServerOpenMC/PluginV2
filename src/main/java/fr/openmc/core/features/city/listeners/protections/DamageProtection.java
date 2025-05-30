package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.features.city.mascots.MascotUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageProtection implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        Entity victim = event.getEntity();
        Entity damager = event.getDamager();

        Player attacker = null;
        if (damager instanceof Player p) {
            attacker = p;
        } else if (damager instanceof Projectile proj && proj.getShooter() instanceof Player shooter) {
            attacker = shooter;
        }

        if (victim instanceof Player victimPlayer && attacker != null) {
            Location loc = victimPlayer.getLocation();
            City city = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());

            if (city != null
                    && city.isMember(victimPlayer)
                    && city.isMember(attacker)) {

                if (!city.getLaw().isPvp()) {
                    event.setCancelled(true);
                    return;
                }
                return;
            }
        }

        if (victim instanceof Player victimPlayer) {
            ProtectionsManager.verify(victimPlayer, event, victimPlayer.getLocation());
            if (event.isCancelled()) return;
        }

        if (MascotUtils.isMascot(victim)) return;

        if (attacker != null) {
            ProtectionsManager.verify(attacker, event, victim.getLocation());
        }
    }
}
