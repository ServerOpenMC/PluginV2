package fr.openmc.core.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.HappyGhast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class HappyGhastListener implements Listener {
    private final double DEFAULT_FLYING_SPEED = 0.05;

    @EventHandler
    public void onHappyGhastSpawn(VehicleEnterEvent event) {
        System.out.println("deed");
        if (event.getVehicle() instanceof HappyGhast mob) {
            if (mob.getAttribute(Attribute.FLYING_SPEED) == null) {
                return;
            }
            mob.getAttribute(Attribute.FLYING_SPEED).setBaseValue(DEFAULT_FLYING_SPEED * 1.5);
        }
    }

}
