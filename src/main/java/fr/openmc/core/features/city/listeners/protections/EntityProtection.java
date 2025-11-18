package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Merchant;

public class EntityProtection implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        ProtectionsManager.verify(event.getPlayer(), event, event.getRightClicked().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    void onShear(PlayerShearEntityEvent event) {
        ProtectionsManager.verify(event.getPlayer(), event, event.getEntity().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityInventoryOpen(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof Merchant || entity instanceof InventoryHolder) {
            ProtectionsManager.verify(player, event, entity.getLocation());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof Enderman enderman)) return;
        ProtectionsManager.verify(enderman, event, enderman.getLocation());
    }
}
