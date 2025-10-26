package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.ProtectionsManager;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public class VehicleProtection implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player player)) return;

        if (MascotUtils.canBeAMascot(event.getVehicle())) return;

        ProtectionsManager.verify(player, event, event.getVehicle().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        if (!(victim instanceof Vehicle)) return;

        if (MascotUtils.canBeAMascot(victim)) return;

        Entity damager = event.getDamager();
        Player player = null;

        if (damager instanceof Player p) {
            player = p;
        } else if (damager instanceof Projectile proj && proj.getShooter() instanceof Player shooter) {
            player = shooter;
        }

        if (player != null) {
            ProtectionsManager.verify(player, event, victim.getLocation());
        }
    }
}
