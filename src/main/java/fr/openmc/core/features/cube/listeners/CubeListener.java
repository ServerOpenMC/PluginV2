package fr.openmc.core.features.cube.listeners;

import fr.openmc.core.features.cube.Cube;
import fr.openmc.core.features.cube.events.CubeDisableBubbleEvent;
import fr.openmc.core.features.cube.events.CubeEnableBubbleEvent;
import fr.openmc.core.features.cube.events.EnterCubeZoneEvent;
import fr.openmc.core.features.cube.events.ExitCubeZoneEvent;
import fr.openmc.core.features.cube.multiblocks.MultiBlock;
import fr.openmc.core.features.cube.multiblocks.MultiBlockManager;
import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
    public void onPlayerQuit(PlayerJoinEvent event) {
        updatePlayerBubbleState(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            if (!(mb instanceof Cube cube)) continue;

            Location center = cube.getCenter();
            double radius = cube.RADIUS_BUBBLE;

            if (!player.getWorld().equals(center.getWorld())) continue;

            if (player.getLocation().distance(center) <= radius) onPlayerExitBubble(player);
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
        updatePlayerBubbleState(event.getPlayer());
    }

    @EventHandler
    public void onCubeBubbleStart(CubeEnableBubbleEvent event) {
        Cube cube = event.getCube();

        Location center = cube.getCenter();
        double radius = cube.RADIUS_BUBBLE;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().equals(center.getWorld())) continue;

            if (player.getLocation().distance(center) <= radius) {
                updatePlayerBubbleState(player);
            }
        }
    }

    @EventHandler
    public void onCubeBubbleStop(CubeDisableBubbleEvent event) {
        Cube cube = event.getCube();

        Location center = cube.getCenter();
        double radius = cube.RADIUS_BUBBLE;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().equals(center.getWorld())) continue;

            if (player.getLocation().distance(center) <= radius) {
                updatePlayerBubbleState(player);
            }
        }
    }

    public void updatePlayerBubbleState(Player player) {
        if (!player.getLocation().getWorld().getName().equals("world")
                && !DreamUtils.isInDreamWorld(player)) return;

        UUID uuid = player.getUniqueId();

        boolean insideAny = false;
        Cube bestCube = null;

        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            if (!(mb instanceof Cube cube)) continue;

            if (cube.corruptedBubbleTask == null) continue;

            if (!player.getWorld().equals(cube.getCenter().getWorld())) continue;

            double dist = player.getLocation().distance(cube.getCenter());

            if (dist <= cube.RADIUS_BUBBLE) {
                insideAny = true;
                bestCube = cube;
                break;
            }
        }

        boolean alreadyInside = playersInBubble.contains(uuid);

        if (insideAny && !alreadyInside) {
            playersInBubble.add(uuid);
            Bukkit.getPluginManager().callEvent(new EnterCubeZoneEvent(player, bestCube));
            onPlayerEnterBubble(player);
            return;
        }

        if (!insideAny && alreadyInside) {
            playersInBubble.remove(uuid);
            onPlayerExitBubble(player);
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
}