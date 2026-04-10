package fr.openmc.core.features.cube.listeners;

import fr.openmc.core.features.cube.Cube;
import fr.openmc.core.features.cube.events.EnterCubeZoneEvent;
import fr.openmc.core.features.cube.events.ExitCubeZoneEvent;
import fr.openmc.core.features.cube.multiblocks.MultiBlock;
import fr.openmc.core.features.cube.multiblocks.MultiBlockManager;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CubeListener implements Listener {
    private final Set<UUID> playersInBubble = new HashSet<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            if (!(mb instanceof Cube cube)) continue;

            Location clickedBlock = event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : null;
            if (cube.isPartOf(clickedBlock)) {
                cube.repulsePlayer(event.getPlayer(), false);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            if (!(mb instanceof Cube cube)) continue;

            Location belowPlayer = player.getLocation().clone().subtract(0, 1, 0);
            if (cube.isPartOf(belowPlayer)) {
                cube.repulsePlayer(event.getPlayer(), true);
            }
        }
    }

    @EventHandler
    public void onPlayerEnterAndLeaveCubeZone(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.getLocation().getWorld().getName().equals("world")
                && !player.getLocation().getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        boolean insideAny = false;
        Cube cube = null;

        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            if (!(mb instanceof Cube loopCube)) continue;

            Location center = loopCube.getCenter();
            double radius = loopCube.RADIUS_BUBBLE;

            if (!player.getWorld().equals(center.getWorld())) continue;
            cube = loopCube;

            if (player.getLocation().distance(center) <= radius) {
                insideAny = true;
                break;
            }
        }
        
        if (cube == null) {
            throw new NullPointerException("No Cube found in world: " + player.getLocation().getWorld().getName());
        }

        UUID uuid = player.getUniqueId();

        if (insideAny && !playersInBubble.contains(uuid)) {
            playersInBubble.add(uuid);
            Bukkit.getPluginManager().callEvent(new EnterCubeZoneEvent(player, cube));
            if (cube.corruptedBubbleTask != null) onPlayerEnterBubble(player);
        } else if (!insideAny && playersInBubble.contains(uuid)) {
            playersInBubble.remove(uuid);
            if (cube.corruptedBubbleTask != null) onPlayerExitBubble(player);
            Bukkit.getPluginManager().callEvent(new ExitCubeZoneEvent(player));
        }
    }
    
    public void onPlayerEnterBubble(Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.GRAVITY);
        if (attr == null) return;
        
        attr.setBaseValue(0.04);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, 2, true, false, true));
    }
    
    public void onPlayerExitBubble(Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.GRAVITY);
        if (attr == null) return;
        
        attr.setBaseValue(0.08);
        player.removePotionEffect(PotionEffectType.JUMP_BOOST);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            if (!(mb instanceof Cube cube)) continue;

            if (cube.corruptedBubbleTask == null) continue;

            Location center = cube.getCenter();
            double radius = cube.RADIUS_BUBBLE;

            if (!player.getWorld().equals(center.getWorld())) continue;

            boolean inside = player.getLocation().distance(center) <= radius;

            if (!inside) continue;

            AttributeInstance attr = player.getAttribute(Attribute.GRAVITY);
            if (attr != null) {
                attr.setBaseValue(0.08);
            }
            player.removePotionEffect(PotionEffectType.JUMP_BOOST);
        }
    }
}