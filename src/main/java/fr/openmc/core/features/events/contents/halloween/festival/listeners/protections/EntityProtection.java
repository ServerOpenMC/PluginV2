package fr.openmc.core.features.events.contents.halloween.festival.listeners.protections;

import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import io.papermc.paper.event.entity.EntityBreakByEntityEvent;
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

// dupli
public class EntityProtection implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (!event.getPlayer().getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    void onShear(PlayerShearEntityEvent event) {
        if (!event.getPlayer().getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityInventoryOpen(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof Merchant || entity instanceof InventoryHolder) {
            if (!event.getPlayer().getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
                return;

            if (!event.getPlayer().isOp())
                event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof Enderman enderman)) return;
        if (!enderman.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityBreak(EntityBreakByEntityEvent event) {
        if (!event.getRemover().getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        event.setCancelled(true);
    }
}
