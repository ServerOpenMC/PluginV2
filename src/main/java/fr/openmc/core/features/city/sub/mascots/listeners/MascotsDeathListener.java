package fr.openmc.core.features.city.sub.mascots.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.Mascot;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.features.city.sub.war.WarManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static fr.openmc.core.features.city.sub.mascots.MascotsManager.DEAD_MASCOT_NAME;

public class MascotsDeathListener implements Listener {
    @EventHandler
    void onMascotDied(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        Player killer = e.getEntity().getKiller();

        if (!MascotUtils.isMascot(entity)) return;

        PersistentDataContainer data = entity.getPersistentDataContainer();
        String city_uuid = data.get(MascotsManager.mascotsKey, PersistentDataType.STRING);

        City city = CityManager.getCity(city_uuid);

        if (city == null) return;

        Mascot mascot = city.getMascot();

        if (mascot == null) return;

        mascot.setImmunity(true);
        mascot.setAlive(false);

        entity.customName(Component.text(DEAD_MASCOT_NAME));
        entity.setGlowing(true);
        e.setCancelled(true);

        if (killer == null) return;

        City cityEnemy = CityManager.getPlayerCity(killer.getUniqueId());

        if (cityEnemy == null) return;

        if (cityEnemy.isInWar() && city.isInWar()) {
            War warEnemy = cityEnemy.getWar();
            War war = city.getWar();

            if (!war.equals(warEnemy)) return;

            WarManager.endWar(war);
        } else {
            // todo: systeme de vulnerabilité d'une ville, check si la ville attaqué est vulnérable, si oui la ville attaqué est supprimé
        }
    }
}
