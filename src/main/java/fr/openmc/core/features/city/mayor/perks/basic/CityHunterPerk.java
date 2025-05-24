package fr.openmc.core.features.city.mayor.perks.basic;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CityHunterPerk implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }


        City attackerCity = CityManager.getPlayerCity(attacker.getUniqueId());
        if (attackerCity == null) {
            return;
        }

        if (attackerCity.getMayor() == null) return;

        if (MayorManager.getInstance().phaseMayor != 2) return;

        if (!PerkManager.hasPerk(attackerCity.getMayor(), Perks.CITY_HUNTER.getId())) {
            return;
        }

        Entity target = event.getEntity();

        if (!(target instanceof Player) && !(target instanceof Monster)) {
            return;
        }

        if (CityManager.getCityFromChunk(target.getChunk().getX(), target.getChunk().getZ()) != null && CityManager.getCityFromChunk(target.getChunk().getX(), target.getChunk().getZ()) != attackerCity) {
            return;
        }

        double baseDamage = event.getDamage();
        double newDamage = baseDamage * 1.20;

        event.setDamage(newDamage);
    }
}
