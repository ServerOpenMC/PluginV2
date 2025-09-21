package fr.openmc.core.features.cube.listeners;

import fr.openmc.core.features.cube.Cube;
import fr.openmc.core.features.cube.multiblocks.MultiBlock;
import fr.openmc.core.features.cube.multiblocks.MultiBlockManager;
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

public class CubeListener implements Listener {

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
    public void onPlayerEnterAndLeaveBubble(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            if (!(mb instanceof Cube cube)) continue;

            if (cube.corruptedBubbleTask == null) continue;

            Location center = cube.getCenter();
            double radius = cube.RADIUS_BUBBLE;

            if (!player.getWorld().equals(center.getWorld())) continue;

            boolean inside = player.getLocation().distance(center) <= radius;
            AttributeInstance attr = player.getAttribute(Attribute.GRAVITY);
            if (attr == null) continue;

            if (inside) {
                attr.setBaseValue(0.04);
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 200, 2, true, false, true));
            } else {
                attr.setBaseValue(0.08);
                player.removePotionEffect(PotionEffectType.JUMP_BOOST);
            }
        }
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