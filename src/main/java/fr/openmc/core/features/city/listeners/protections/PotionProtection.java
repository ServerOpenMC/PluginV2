package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.projectiles.ProjectileSource;

public class PotionProtection implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        ProjectileSource shooter = potion.getShooter();

        if (!(shooter instanceof Witch witch))
            return;

        Location witchLocation = witch.getLocation();
        City city = CityManager.getCityFromChunk(witchLocation.getChunk().getX(), witchLocation.getChunk().getZ());
        if (city == null)
            return;

        boolean isCityInWar = city.getType().equals(CityType.WAR);

        for (LivingEntity affectedEntity : event.getAffectedEntities()) {
            if (!(affectedEntity instanceof Player player))
                continue;

            boolean isNotMember = !city.isMember(player);
            if (!isNotMember || isCityInWar)
                continue;

            event.setCancelled(true);
        }
    }
}
