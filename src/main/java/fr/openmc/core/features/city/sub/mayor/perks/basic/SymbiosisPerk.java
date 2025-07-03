package fr.openmc.core.features.city.sub.mayor.perks.basic;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class SymbiosisPerk implements Listener {

    private static final double SQUARED_RADIUS = 10.0 * 10.0;

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) return;

        if (!PerkManager.hasPerk(playerCity.getMayor(), Perks.SYMBIOSIS.getId())) return;

        LivingEntity mascot = (LivingEntity) Bukkit.getEntity(playerCity.getMascot().getMascotUUID());

        if (mascot == null || !mascot.isValid()) return;

        if (!player.getLocation().getWorld().equals(mascot.getLocation().getWorld())) return;

        if (player.getLocation().distanceSquared(mascot.getLocation()) <= SQUARED_RADIUS) {
            double originalDamage = event.getDamage();
            double reducedDamage = originalDamage * 0.85;
            event.setDamage(reducedDamage);
        }
    }
}
