package fr.openmc.core.features.dream.registries.mobs.listeners;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Listener pour la gestion de l'apparition de mobs dans le biome MUD_BEACH
 * dans la dimension Dream.
 */
public class MudBeachMobSpawningListener implements Listener {

    private final double CORRUPTED_TADPOLE_PROBABILITY = 0.05;

    /**
     * Gère l'événement de spawn de créature.
     * <p>
     * L'événement est annulé si la créature se trouve dans le biome MUD_BEACH
     * de la dimension Dream et qu'un mob est généré selon une probabilité définie dans la classe.
     * </p>
     *
     * @param e l'événement de spawn de créature
     */
    @EventHandler
    void onCreatureSpawn(CreatureSpawnEvent e) {
        if (DreamMobsRegistry.isDreamMob(e.getEntity())) return;

        Location spawningLoc = e.getEntity().getLocation();

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        World world = spawningLoc.getWorld();
        if (!DreamUtils.isDreamWorld(world)) return;

        e.setCancelled(true);

        if (!world.getBiome(spawningLoc).equals(DreamBiome.MUD_BEACH.getBiome())) return;

        double choice = Math.random();
        if (choice < CORRUPTED_TADPOLE_PROBABILITY) {
            DreamMobsRegistry.getByName("corrupted_tadpole").spawn(spawningLoc);
            e.setCancelled(true);
        }
    }
}
