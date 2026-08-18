package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;

import java.util.UUID;

public class MountProtection implements Listener {
    private final ProtectionsManager protectionsManager;

    public MountProtection(CityManager cityManager) {
        this.protectionsManager = cityManager.PROTECTIONS;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityMount(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Entity mount = event.getMount();

        if (mount instanceof Tameable tameable) {
            if (!tameable.isTamed()) return;

            UUID ownerUUID = tameable.getOwnerUniqueId();

            if (ownerUUID == null) return;

            if (!ownerUUID.equals(player.getUniqueId())) {
                if (!protectionsManager.canInteract(player, mount.getLocation())) {
                    event.setCancelled(true);
                    protectionsManager.cancelMessage(player);
                } else {
                    protectionsManager.verify(player, event, mount.getLocation());
                }
            }
        } else {
            protectionsManager.verify(player, event, mount.getLocation());
        }
    }
}
