package fr.openmc.core.features.events.contents.halloween.festival.listeners.protections;

import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

// dupli
public class VehicleProtection implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player player)) return;

        if (!player.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        if (!player.isOp())
            event.setCancelled(true);
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
            if (!player.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
                return;

            if (!player.isOp())
                event.setCancelled(true);
        }
    }
}
