package fr.openmc.core.features.events.contents.christmas.mecanism.rift.listeners;

import fr.openmc.core.features.events.contents.christmas.mecanism.rift.RiftManager;
import fr.openmc.core.registry.structures.events.CustomStructurePlaceEvent;
import fr.openmc.core.registry.structures.events.CustomStructureRemoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class StructureListener implements Listener {
    @EventHandler
    public void onStructurePlace(CustomStructurePlaceEvent event) {
        if (RiftManager.getRifts().contains(event.getPlacementId())) {
            RiftManager.registerRegion(event.getPlacementId(), event.getWorld(),
                    event.getMin(), event.getMax());
        }
    }

    @EventHandler
    public void onStructureRemove(CustomStructureRemoveEvent event) {
        if (RiftManager.getRifts().contains(event.getPlacementId())) {
            RiftManager.unregisterRegion(event.getPlacementId());
        }
    }
}
