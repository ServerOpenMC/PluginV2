package fr.openmc.core.features.dream.mobs.listeners;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.mobs.DreamMobManager;
import fr.openmc.core.features.dream.mobs.mobs.DreamCreaking;
import fr.openmc.core.features.dream.mobs.mobs.DreamSpider;
import org.bukkit.Location;
import org.bukkit.entity.Creaking;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Listener pour la gestion de l'apparition de mobs dans le biome SCULK_PLAINS
 * dans la dimension Dream.
 */
public class PlainsMobSpawningListener implements Listener {

    private final double DREAM_SPIDER_PROBABILITY = 0.7;

    /**
     * Gère l'événement de spawn de créature.
     * <p>
     * L'événement est annulé si la créature se trouve dans le biome SCULK_PLAINS
     * de la dimension Dream et qu'un mob est généré selon une probabilité définie dans le {@link DreamMobManager}.
     * </p>
     *
     * @param e l'événement de spawn de créature
     */
    @EventHandler
    void onCreatureSpawn(CreatureSpawnEvent e) {
        if (DreamMobManager.isDreamMob(e.getEntity())) {
            return;
        }

        Location spawningLoc = e.getEntity().getLocation();

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        if (!spawningLoc.getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;
        if (!spawningLoc.getWorld().getBiome(spawningLoc).equals(DreamBiome.SCULK_PLAINS.getBiome())) return;

        if (e.getEntity().getType().equals(EntityType.CREAKING)) {
            new DreamCreaking().apply((Creaking) e.getEntity());
            return;
        }

        double choice = Math.random();

        if (e.getEntity().isOnGround() && choice < DREAM_SPIDER_PROBABILITY) {
            new DreamSpider().spawn(spawningLoc);
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
    }
}
