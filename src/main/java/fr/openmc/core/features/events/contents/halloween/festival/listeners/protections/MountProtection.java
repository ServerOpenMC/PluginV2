package fr.openmc.core.features.events.contents.halloween.festival.listeners.protections;

import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;

import java.util.UUID;

// dupli
public class MountProtection implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntityMount(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Entity mount = event.getMount();

        if (mount instanceof Tameable tameable) {
            if (!tameable.isTamed()) return;

            UUID ownerUUID = tameable.getOwnerUniqueId();

            if (ownerUUID == null) return;

            if (!player.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
                return;

            if (!player.isOp())
                event.setCancelled(true);
        } else {
            if (!player.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
                return;

            if (!player.isOp())
                event.setCancelled(true);
        }
    }
}
