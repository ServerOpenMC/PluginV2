package fr.openmc.core.features.city.sub.mascots.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.features.city.sub.war.WarManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.List;

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

        mascot.setAlive(false);
        entity.customName(Component.text(DEAD_MASCOT_NAME));

        e.setCancelled(true);

        if (killer == null) return;

        City cityEnemy = CityManager.getPlayerCity(killer.getUniqueId());

        if (cityEnemy == null) return;

        if (cityEnemy.isInWar() && city.isInWar()) {
            War warEnemy = cityEnemy.getWar();
            War war = city.getWar();

            if (!war.equals(warEnemy)) return;

            spawnFireworkExplosion(entity.getLocation());

            List<Player> nearbyEnemies = entity.getNearbyEntities(5, 5, 5).stream()
                    .filter(Player.class::isInstance)
                    .map(Player.class::cast)
                    .toList();

            for (Player player : nearbyEnemies) {
                Vector direction = player.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
                direction.setY(1);
                player.setVelocity(direction);
            }

            WarManager.endWar(war);
        } else {
            // TODO: systeme de vulnerabilité d'une ville, check si la ville attaqué est vulnérable, si oui la ville attaqué est supprimé
        }
    }

    public void spawnFireworkExplosion(Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();

        meta.addEffect(FireworkEffect.builder()
                .flicker(true)
                .trail(true)
                .withColor(Color.RED, Color.BLUE)
                .withFade(Color.WHITE)
                .with(FireworkEffect.Type.STAR)
                .build());

        meta.setPower(0);
        firework.setFireworkMeta(meta);

        firework.detonate();
    }
}
