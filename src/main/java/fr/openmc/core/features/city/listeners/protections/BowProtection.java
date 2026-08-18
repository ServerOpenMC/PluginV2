package fr.openmc.core.features.city.listeners.protections;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

public class BowProtection implements Listener {
    private final ProtectionsManager protectionsManager;

    public BowProtection(CityManager cityManager) {
        this.protectionsManager = cityManager.PROTECTIONS;
    }

    @EventHandler(ignoreCancelled = true)
    void onLaunchProjectile(PlayerLaunchProjectileEvent event) {
        protectionsManager.verify(event.getPlayer(), event, event.getPlayer().getLocation());
    }
    
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getConsumable() == null) return;
        
        protectionsManager.verify(player, event, event.getEntity().getLocation());

        if (event.isCancelled() && !player.getGameMode().equals(GameMode.CREATIVE)) {
            player.getInventory().addItem(event.getConsumable());
        }
    }
}
