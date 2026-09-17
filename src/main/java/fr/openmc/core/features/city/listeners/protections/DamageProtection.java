package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.ProtectionsManager;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageProtection implements Listener {
    private final ProtectionsManager protectionsManager;

    public DamageProtection() {
        this.protectionsManager = OMCRegistry.CITY_FEATURES.PROTECTIONS;
    }

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
            Location loc = victimPlayer.getLocation();
            City city = City.of(loc);

            if (city != null
                    && city.isMember(victimPlayer)
                    && city.isMember(attacker)) {

                if (!city.getLaw().isPvp() || city.isInWar()) {
                    event.setCancelled(true);
                    return;
                }
                return;
            }
        }

        if (victim instanceof Player victimPlayer) {
            protectionsManager.verify(victimPlayer, event, victimPlayer.getLocation());
        }

        if (MascotUtils.canBeAMascot(victim)) return;

        if (attacker != null) {
            protectionsManager.verify(attacker, event, victim.getLocation());
        }
    }
}
