package fr.openmc.core.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class HappyGhastListener implements Listener {
    private final double DEFAULT_FLYING_SPEED = 0.05;

    @EventHandler(ignoreCancelled = true)
    public void onHappyGhastSpawn(EntitySpawnEvent event) {
        if (event.getEntityType() != EntityType.HAPPY_GHAST) return;

        LivingEntity entity = (LivingEntity) event.getEntity();

        AttributeInstance attr = entity.getAttribute(Attribute.FLYING_SPEED);
        if (attr == null) return;

        double multiplier = getMultiplier();
        attr.setBaseValue(DEFAULT_FLYING_SPEED * multiplier);
    }

    // Effet de rareté, plus c'est proche de 1.6, plus c'est rare
    private double getMultiplier() {
        double min = 1.0;
        double max = 1.6;
        double random = Math.random();
        double biased = 1 - Math.pow(random, 3);
        return min + (max - min) * biased;
    }

}
