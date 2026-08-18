package fr.openmc.core.features.city.sub.mayor.perks.basic;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.perks.PerkUtils;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CityHunterPerk implements Listener {
    private final CityManager cityManager;

    public CityHunterPerk() {
        this.cityManager = OMCRegistry.FEATURES.CITY.get();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;


        City attackerCity = cityManager.getPlayerCity(attacker.getUniqueId());
        if (attackerCity == null) return;

        if (attackerCity.getMayor() == null) return;

        if (cityManager.MAYOR.phaseMayor != 2) return;

        if (!PerkUtils.hasPerk(attackerCity.getMayor(), Perks.CITY_HUNTER.getId())) return;


        Entity target = event.getEntity();

        if (!(target instanceof Player) && !(target instanceof Monster)) return;

        if (cityManager.getCityFromChunk(target.getChunk().getX(), target.getChunk().getZ()) != null
                && (cityManager.getCityFromChunk(
                        target.getChunk().getX(),
                        target.getChunk().getZ())
                != attackerCity)
        )
            return;


        double baseDamage = event.getDamage();
        double newDamage = baseDamage * 1.20;

        event.setDamage(newDamage);
    }
}
