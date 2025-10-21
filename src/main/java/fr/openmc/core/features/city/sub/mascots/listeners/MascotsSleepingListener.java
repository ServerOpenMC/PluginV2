package fr.openmc.core.features.city.sub.mascots.listeners;

import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.Collection;

public class MascotsSleepingListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.NOT_SAFE) return;

        Player player = event.getPlayer();
        Location bedLocation = event.getBed().getLocation();

        double radiusX = 8.0;
        double radiusY = 5.0;
        double radiusZ = 8.0;

        Collection<Entity> nearby = player.getWorld().getNearbyEntities(bedLocation, radiusX, radiusY, radiusZ, e -> e instanceof Monster);

        boolean realThreat = nearby.stream()
                .filter(entity -> entity instanceof LivingEntity)
                .anyMatch(entity -> !MascotUtils.canBeAMascot(entity));

        if (!realThreat) {
            event.setCancelled(false);
            event.setUseBed(Event.Result.ALLOW);
        }
    }
}
