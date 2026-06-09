package fr.openmc.core.features.dream.registries.mobs.listeners;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.registries.DreamBiome;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import fr.openmc.core.registry.mobs.CustomMobRegistry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Listener pour la gestion de l'apparition de mobs dans le biome SCULK_PLAINS
 * dans la dimension Dream.
 */
public class PlainsMobSpawningListener implements Listener {

    private final double DREAM_SPIDER_PROBABILITY = 0.05;

    /**
     * Gère l'événement de spawn de créature.
     * <p>
     * L'événement est annulé si la créature se trouve dans le biome SCULK_PLAINS
     * de la dimension Dream et qu'un mob est généré selon une probabilité définie dans le {@link DreamMobsRegistry}.
     * </p>
     *
     * @param e l'événement de spawn de créature
     */
    @EventHandler(priority = EventPriority.MONITOR)
    void onCreatureSpawn(CreatureSpawnEvent e) {
        if (CustomMobRegistry.isCustomMob(e.getEntity())) return;

        Location spawningLoc = e.getEntity().getLocation();

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        World world = spawningLoc.getWorld();
        if (!DreamUtils.isDreamWorld(world)) return;
        e.setCancelled(true);
        if (!DreamBiome.isDreamBiome(spawningLoc, DreamBiome.SCULK_PLAINS)) return;

        if (e.getEntity().getType().equals(EntityType.CREAKING)) {
            e.setCancelled(false);
            DreamMobsRegistry.DREAM_CREAKING.apply(e.getEntity());
            return;
        }

        double choice = Math.random();
        if (choice < DREAM_SPIDER_PROBABILITY) {
            DreamMobsRegistry.DREAM_SPIDER.spawn(spawningLoc);
            e.setCancelled(true);
        }
    }
}
