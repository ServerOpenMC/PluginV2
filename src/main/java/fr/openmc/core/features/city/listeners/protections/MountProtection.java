package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;

public class MountProtection implements Listener {
    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        Entity mount = event.getMount();

        if (!(mount instanceof Tameable tameable)) return;


        if (!tameable.isTamed()) return;

        if (tameable.getOwnerUniqueId() == null) {
            ProtectionsManager.verify(player, event, mount.getLocation());
            return;
        }

        if (!tameable.getOwnerUniqueId().equals(player.getUniqueId()))
            ProtectionsManager.verify(player, event, mount.getLocation());
    }
}
