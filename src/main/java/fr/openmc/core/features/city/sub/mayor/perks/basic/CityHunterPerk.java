package fr.openmc.core.features.city.sub.mayor.perks.basic;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mayor.models.MayorPhase;
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


        City attackerCity = City.ofPlayer(attacker);
        if (attackerCity == null) return;

        if (attackerCity.getMayor() == null) return;

        if (!attackerCity.getMayorPhase().equals(MayorPhase.MAYOR_ELECTED)) return;

        if (!PerkUtils.hasPerk(attackerCity.getMayor(), Perks.CITY_HUNTER.getId())) return;


        Entity target = event.getEntity();

        if (!(target instanceof Player) && !(target instanceof Monster)) return;

        City chunkCity = City.of(target.getChunk());
        if (chunkCity != null && !chunkCity.equals(attackerCity)) return;

        double baseDamage = event.getDamage();
        double newDamage = baseDamage * 1.20;

        event.setDamage(newDamage);
    }
}
