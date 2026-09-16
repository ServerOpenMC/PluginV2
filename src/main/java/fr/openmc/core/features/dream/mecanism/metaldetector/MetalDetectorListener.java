package fr.openmc.core.features.dream.mecanism.metaldetector;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.registries.DreamBiome;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class MetalDetectorListener implements Listener {

    private final MetalDetectorManager manager;

    public MetalDetectorListener(MetalDetectorManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        if (DreamBiome.isInDreamBiome(player, DreamBiome.MUD_BEACH)) {
            if (!manager.hiddenChests.containsKey(player.getUniqueId())) {
                Location chestLoc = manager.findRandomChestLocation(loc);
                MetalDetectorTask task = new MetalDetectorTask(player, chestLoc);
                task.runTaskTimer(OMCPlugin.getInstance(), 0L, 5L);
                manager.hiddenChests.put(player.getUniqueId(), task);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (manager.hiddenChests.containsKey(uuid))
            manager.hiddenChests.remove(uuid).cancel();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (manager.hiddenChests.containsKey(uuid)) {
            MetalDetectorTask oldTask = manager.hiddenChests.get(uuid);
            Location newLoc = manager.findRandomChestLocation(player.getLocation());
            MetalDetectorTask newTask = new MetalDetectorTask(player, newLoc);
            newTask.runTaskTimer(OMCPlugin.getInstance(), 0L, 5L);
            manager.hiddenChests.put(uuid, newTask);
            oldTask.cancel();
        }
    }
}
