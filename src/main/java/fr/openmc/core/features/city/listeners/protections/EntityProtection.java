package fr.openmc.core.features.city.listeners.protections;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.ProtectionsManager;
import io.papermc.paper.event.entity.EntityCollideWithEntityEvent;
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
    private final ProtectionsManager protectionsManager;

    public EntityProtection(CityManager cityManager) {
        this.protectionsManager = cityManager.PROTECTIONS;
    }

    @EventHandler(ignoreCancelled = true)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        protectionsManager.verify(event.getPlayer(), event, event.getRightClicked().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    void onShear(PlayerShearEntityEvent event) {
        protectionsManager.verify(event.getPlayer(), event, event.getEntity().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityInventoryOpen(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof Merchant || entity instanceof InventoryHolder) {
            protectionsManager.verify(player, event, entity.getLocation());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof Enderman enderman)) return;
        protectionsManager.verify(enderman, event, enderman.getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
        if (!(event.getHitBy() instanceof Player player)) return;
        protectionsManager.verify(player, event, event.getEntity().getLocation());
    }

    @EventHandler
    public void onPlayerCollideEntity(EntityCollideWithEntityEvent event) {
        if (!(event.getEntities().getFirst() instanceof Player player)) return;
        protectionsManager.verify(player, event, event.getEntities().get(1).getLocation());
    }
}
