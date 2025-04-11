package fr.openmc.core.features.city.mayor.perks;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mascots.MascotUtils;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class SymbiosisPerk implements Listener {

    private static final double RADIUS = 10.0;

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            return;
        }

        if (!PerkManager.hasPerk(playerCity.getMayor(), 10)) return;

        LivingEntity mascot = (LivingEntity) Bukkit.getEntity(MascotUtils.getMascotUUIDOfCity(playerCity.getUUID()));
        if (mascot == null || !mascot.isValid()) {
            return;
        }

        if (player.getLocation().distance(mascot.getLocation()) <= RADIUS) {
            double originalDamage = event.getDamage();
            double reducedDamage = originalDamage * 0.85;
            event.setDamage(reducedDamage);
        }
    }
}
