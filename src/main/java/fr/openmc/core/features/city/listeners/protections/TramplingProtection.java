package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TramplingProtection implements Listener {
    private final ProtectionsManager protectionsManager;

    public TramplingProtection(CityManager cityManager) {
        this.protectionsManager = cityManager.PROTECTIONS;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityInteract(EntityInteractEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND) {
            protectionsManager.verify(event.getEntity(), event, block.getLocation());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTrampling(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock() == null) return;
            if (event.getClickedBlock().getType() == Material.FARMLAND) {
                protectionsManager.verify(event.getPlayer(), event, event.getClickedBlock().getLocation());
            }
        }
    }
}
