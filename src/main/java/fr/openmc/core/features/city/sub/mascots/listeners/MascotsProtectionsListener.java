package fr.openmc.core.features.city.sub.mascots.listeners;

import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.weather.LightningStrikeEvent;

import java.util.Collection;

public class MascotsProtectionsListener implements Listener {
    @EventHandler
    public void onBlockPlace(CustomBlockPlaceEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();

        Collection<Entity> nearbyEntities = loc.getWorld().getNearbyEntities(loc, 1.5, 1.5, 1.5);

        for (Entity entity : nearbyEntities) {
            if (!MascotUtils.canBeAMascot(entity)) return;

            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!MascotUtils.canBeAMascot(event.getEntity())) return;

        event.setCancelled(true);
    }

    @EventHandler
    void onLightningStrike(LightningStrikeEvent e) {
        Location strikeLocation = e.getLightning().getLocation();

        for (Entity entity : strikeLocation.getWorld().getNearbyEntities(strikeLocation, 3, 3, 3)) {
            if (!(entity instanceof LivingEntity)) continue;

            if (!MascotUtils.canBeAMascot(entity)) continue;

            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    void onPistonExtend(BlockPistonExtendEvent e) {
        Location pistonHeadLocation = e.getBlock().getRelative(e.getDirection()).getLocation();
        for (Entity entity : pistonHeadLocation.getWorld().getNearbyEntities(pistonHeadLocation, 0.5, 0.5, 0.5)) {
            if (!(entity instanceof LivingEntity)) continue;
            if (!MascotUtils.canBeAMascot(entity)) continue;

            e.setCancelled(true);
            return;
        }
        for (Block block : e.getBlocks()) {
            Location futureLocation = block.getRelative(e.getDirection()).getLocation();
            for (Entity entity : block.getWorld().getNearbyEntities(futureLocation, 0.5, 0.5, 0.5)) {
                if (!(entity instanceof LivingEntity)) continue;
                if (!(MascotUtils.canBeAMascot(entity))) continue;

                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    void onTransform(EntityTransformEvent event) {
        Entity entity = event.getEntity();
        if (!MascotUtils.canBeAMascot(entity)) return;

        event.setCancelled(true);
    }

    @EventHandler
    void onPortal(EntityPortalEvent event) {
        Entity entity = event.getEntity();
        if (!MascotUtils.canBeAMascot(entity)) return;

        event.setCancelled(true);
    }

    @EventHandler
    void onFire(EntityCombustEvent e) {
        Entity entity = e.getEntity();
        if (!MascotUtils.canBeAMascot(entity)) return;

        e.setCancelled(true);
    }

    @EventHandler
    void onPigMount(EntityMountEvent e) {
        Entity entity = e.getMount();
        if (!MascotUtils.canBeAMascot(entity)) return;

        e.setCancelled(true);
    }

    @EventHandler
    void onMove(EntityMoveEvent e) {
        Entity entity = e.getEntity();
        if (!MascotUtils.canBeAMascot(entity)) return;

        e.setCancelled(true);
    }

    @EventHandler
    void onAxolotlBucket(PlayerBucketEntityEvent e) {
        Entity entity = e.getEntity();
        if (!MascotUtils.canBeAMascot(entity)) return;

        e.setCancelled(true);
    }
}
