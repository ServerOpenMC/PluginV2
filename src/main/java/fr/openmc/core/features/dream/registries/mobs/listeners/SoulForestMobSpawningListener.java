package fr.openmc.core.features.dream.registries.mobs.listeners;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.registries.DreamBiome;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import fr.openmc.core.registry.mobs.CustomMobRegistry;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Listener pour la gestion de l'apparition de mobs dans le biome SOUL_FOREST
 * dans la dimension Dream.
 */
public class SoulForestMobSpawningListener implements Listener {

    private final double SOUL_PROBABILITY = 0.5;

    /**
     * Gère l'événement de spawn de créature.
     * <p>
     * L'événement est annulé si la créature se trouve dans le biome SOUL_FOREST
     * de la dimension Dream et qu'un mob est généré selon une probabilité définie dans le {@link DreamMobsRegistry}.
     * </p>
     *
     * @param e l'événement de spawn de créature
     */
    @EventHandler
    void onCreatureSpawn(CreatureSpawnEvent e) {
        if (CustomMobRegistry.isCustomMob(e.getEntity())) return;

        Location spawningLoc = e.getEntity().getLocation();

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        if (!DreamUtils.isDreamWorld(spawningLoc)) return;
        e.setCancelled(true);
        if (!DreamBiome.isDreamBiome(spawningLoc, DreamBiome.SOUL_FOREST)) return;

        double choice = Math.random();

        if (choice < SOUL_PROBABILITY) {
            DreamMobsRegistry.SOUL.spawn(spawningLoc);
            e.setCancelled(true);
        }
    }
}
