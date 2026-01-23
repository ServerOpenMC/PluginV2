package fr.openmc.core.features.dream.mecanism.metaldetector;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.MetalDetectorLootEvent;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.models.registry.loottable.DreamLootTable;
import fr.openmc.core.utils.LocationUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
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
    public void onChestClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null) return;

        if (!DreamUtils.isDreamWorld(event.getClickedBlock().getLocation())) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!hiddenChests.containsKey(uuid)) return;

        if (clicked.getType() == Material.CHEST) {
            event.setCancelled(true);
            MetalDetectorTask task = hiddenChests.remove(uuid);
            task.cancel();
            Location chestLoc = task.getChestLocation();

            if (LocationUtils.isSameLocation(clicked.getLocation(), chestLoc)) {
                event.setCancelled(true);
                clicked.setType(Material.MUD);
                DreamLootTable lootTable = MetalDetectorManager.METAL_DETECTOR_LOOT_TABLE;
                if (lootTable == null) return;
                List<ItemStack> rewards = lootTable.rollLoots();

                for (ItemStack item : rewards) {
                    player.getInventory().addItem(item);
                }

                Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                        Bukkit.getServer().getPluginManager().callEvent(new MetalDetectorLootEvent(player, rewards))
                );
                MessagesManager.sendMessage(player, Component.text("Vous avez découvert §e" + rewards.size() + " §fobjet(s) dans vos rêves !"), Prefix.DREAM, MessageType.SUCCESS, false);
            }
        }
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
            int dx = random.nextInt(20);
            int dz = random.nextInt(20);
            Location tryLoc = origin.clone().add(dx, 0, dz);
            int y = world.getHighestBlockYAt(tryLoc);
            tryLoc.setY(y);

            if (world.getBiome(tryLoc).equals(DreamBiome.MUD_BEACH.getBiome())) {
                return tryLoc;
            }
        }

        return origin.clone().add(5, 0, 5);
    }
}
