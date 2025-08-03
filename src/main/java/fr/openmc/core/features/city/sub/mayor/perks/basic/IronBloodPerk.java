package fr.openmc.core.features.city.sub.mayor.perks.basic;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IronBloodPerk implements Listener {
    private static final Map<City, Long> perkIronBloodCooldown = new HashMap<>();
    private static final long COOLDOWN_TIME = 3 * 60 * 1000L; // 3 minutes

    public static void spawnGolem(Player player, City city, Entity mobMascot) {
        long currentTime = System.currentTimeMillis();
        if (perkIronBloodCooldown.containsKey(city) && currentTime - perkIronBloodCooldown.get(city) < COOLDOWN_TIME) {
            return;
        }
        perkIronBloodCooldown.put(city, currentTime);
        org.bukkit.Location location = mobMascot.getLocation().clone();
        location.add(0, 3, 0);

        IronGolem golem = (IronGolem) location.getWorld().spawnEntity(location, EntityType.IRON_GOLEM);
        golem.setPlayerCreated(false);
        golem.setLootTable(null);
        golem.setGlowing(true);
        golem.setHealth(30);

        Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
            if (!golem.isValid() || golem.isDead()) {
                return;
            }
            List<Player> nearbyEnemies = golem.getNearbyEntities(10, 10, 10).stream()
                    .filter(Player.class::isInstance)
                    .map(Player.class::cast)
                    .filter(nearbyPlayer -> {
                        City enemyCity = CityManager.getPlayerCity(nearbyPlayer.getUniqueId());
                        return enemyCity != null && !enemyCity.getUUID().equals(city.getUUID());
                    })
                    .toList();

            if (!nearbyEnemies.isEmpty()) {
                Player target = nearbyEnemies.getFirst();
                golem.setAI(true);
                golem.setTarget(target);
                Vector direction = target.getLocation().toVector().subtract(golem.getLocation().toVector()).normalize();
                golem.setVelocity(direction.multiply(0.5));
            } else {
                golem.setAI(false);
                golem.setTarget(null);
            }
        }, 0L, 20L);
        scheduleGolemDespawn(golem, mobMascot.getUniqueId());

        MessagesManager.sendMessage(player, Component.text("§8§o*tremblement* Quelque chose semble arriver..."), Prefix.MAYOR, MessageType.INFO, false);

    }

    private static void scheduleGolemDespawn(IronGolem golem, UUID mascotUUID) {
        long delayInitial = 3 * 60 * 20L;  // 3 minutes
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (!golem.isValid() || golem.isDead()) {
                return;
            }

            List<Player> nearbyEnemies = golem.getNearbyEntities(10, 10, 10).stream()
                    .filter(ent -> ent instanceof Player)
                    .map(ent -> (Player) ent)
                    .filter(nearbyPlayer -> {
                        City enemyCity = CityManager.getPlayerCity(nearbyPlayer.getUniqueId());
                        return enemyCity != null && !enemyCity.getUUID().equals(MascotUtils.getCityFromEntity(mascotUUID).getUUID());
                    })
                    .toList();

            if (nearbyEnemies.isEmpty() && golem.getTarget() == null) {
                golem.remove();
            } else {
                Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> scheduleGolemDespawn(golem, mascotUUID), 200L);
            }
        }, delayInitial);
    }
}
