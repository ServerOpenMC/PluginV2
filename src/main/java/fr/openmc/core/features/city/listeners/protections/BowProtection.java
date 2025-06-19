package fr.openmc.core.features.city.listeners.protections;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

public class BowProtection implements Listener {
    @EventHandler
    void onLaunchProjectile(PlayerLaunchProjectileEvent event) {
        ProtectionsManager.verify(event.getPlayer(), event, event.getPlayer().getLocation());
    }
    
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        
        ProtectionsManager.verify(player, event, event.getEntity().getLocation());
    }
}
