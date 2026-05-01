package fr.openmc.core.features.dream.mecanism.metaldetector;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.world.LocationUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Random;
import java.util.UUID;

import static fr.openmc.core.features.dream.mecanism.metaldetector.MetalDetectorManager.hiddenChests;

public class MetalDetectorListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        if (loc.getBlock().getBiome().equals(DreamBiome.MUD_BEACH.getBiome())) {
            if (!hiddenChests.containsKey(player.getUniqueId())) {
                Location chestLoc = findRandomChestLocation(loc);
                MetalDetectorTask task = new MetalDetectorTask(player, chestLoc);
                task.runTaskTimer(OMCPlugin.getInstance(), 0L, 5L);
                hiddenChests.put(player.getUniqueId(), task);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (hiddenChests.containsKey(uuid))
            hiddenChests.remove(uuid).cancel();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (hiddenChests.containsKey(uuid)) {
            MetalDetectorTask oldTask = hiddenChests.get(uuid);
            Location newLoc = findRandomChestLocation(player.getLocation());
            MetalDetectorTask newTask = new MetalDetectorTask(player, newLoc);
            newTask.runTaskTimer(OMCPlugin.getInstance(), 0L, 5L);
            hiddenChests.put(uuid, newTask);
            oldTask.cancel();
        }
    }

    public static Location findRandomChestLocation(Location origin) {
        World world = origin.getWorld();
        Random random = new Random();

        for (int i = 0; i < 30; i++) {
            int dx = random.nextInt(41) - 20;
            int dz = random.nextInt(41) - 20;
            Location tryLoc = origin.clone().add(dx, 0, dz);
            int y = world.getHighestBlockYAt(tryLoc);
            tryLoc.setY(y);

            if (world.getBiome(tryLoc).equals(DreamBiome.MUD_BEACH.getBiome())) {
                return tryLoc;
            }
        }

        return origin.clone().add(random.nextInt(41) - 20, 0, random.nextInt(41) - 20);
    }
}
