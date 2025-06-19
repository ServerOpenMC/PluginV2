package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Merchant;

public class EntityProtection implements Listener {
    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        ProtectionsManager.verify(event.getPlayer(), event, event.getRightClicked().getLocation());
    }

    @EventHandler
    void onShear(PlayerShearEntityEvent event) {
        ProtectionsManager.verify(event.getPlayer(), event, event.getEntity().getLocation());
    }

    @EventHandler
    public void onEntityInventoryOpen(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof Merchant || entity instanceof InventoryHolder) {
            ProtectionsManager.verify(player, event, entity.getLocation());
        }
    }
}
