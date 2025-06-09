package fr.openmc.core.features.city.sub.mascots.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.MascotRegenerationUtils;
import fr.openmc.core.features.city.sub.mascots.Mascot;
import fr.openmc.core.features.city.sub.mascots.MascotUtils;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.sub.mayor.perks.basic.IronBloodPerk;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

import static fr.openmc.core.features.city.sub.mascots.MascotsManager.DEAD_MASCOT_NAME;

public class MascotsDamageListener implements Listener {
    @EventHandler
    void onMascotDamageCaused(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (!MascotUtils.isMascot(entity)) return;

        EntityDamageEvent.DamageCause cause = e.getCause();

        if (cause.equals(EntityDamageEvent.DamageCause.SUFFOCATION) || cause.equals(EntityDamageEvent.DamageCause.FALLING_BLOCK) ||
                cause.equals(EntityDamageEvent.DamageCause.LIGHTNING) || cause.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) ||
                cause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) || cause.equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
            e.setCancelled(true);
        }

        City city = MascotUtils.getCityFromEntity(entity.getUniqueId());
        if (city == null) return;
        LivingEntity mob = (LivingEntity) entity;

        double newHealth = Math.floor(mob.getHealth());
        mob.setHealth(newHealth);
        double maxHealth = mob.getMaxHealth();

        Mascot mascot = city.getMascot();
        if (mascot == null) return;

        double healthAfterDamage = Math.floor(mob.getHealth() - e.getFinalDamage());
        if (healthAfterDamage < 0) healthAfterDamage = 0;

        if (!mascot.isAlive()) {
            mob.customName(Component.text(DEAD_MASCOT_NAME));
        } else {
            mob.customName(Component.text(MascotsManager.PLACEHOLDER_MASCOT_NAME.formatted(
                    city.getName(),
                    healthAfterDamage,
                    maxHealth
            )));
        }
    }


    @SneakyThrows
    @EventHandler
    void onMascotTakeDamage(EntityDamageByEntityEvent e) {
        Entity damageEntity = e.getEntity();
        Entity damager = e.getDamager();
        double baseDamage;

        if (!MascotUtils.isMascot(damageEntity)) return;

        if (!(damager instanceof Player player)) return;

        PersistentDataContainer data = damageEntity.getPersistentDataContainer();
        String pdcCityUUID = data.get(MascotsManager.mascotsKey, PersistentDataType.STRING);

        if (pdcCityUUID == null) return;

        Set<EntityDamageEvent.DamageCause> allowedCauses = Set.of(
                EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK,
                EntityDamageEvent.DamageCause.PROJECTILE
        );

        if (!allowedCauses.contains(e.getCause())) {
            e.setCancelled(true);
            return;
        }

        City city = CityManager.getPlayerCity(player.getUniqueId());
        City cityEnemy = MascotUtils.getCityFromEntity(damageEntity.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            e.setCancelled(true);
            return;
        }

        if (cityEnemy == null) {
            MessagesManager.sendMessage(player, Component.text("§cErreur : La ville enemie n'a pas été reconnu"), Prefix.CITY, MessageType.ERROR, false);
            e.setCancelled(true);
            return;
        }
        String city_uuid = city.getUUID();
        String cityEnemy_uuid = cityEnemy.getUUID();

        CityType city_type = city.getType();
        CityType cityEnemy_type = cityEnemy.getType();

        if (city_type == null) {
            MessagesManager.sendMessage(player, Component.text("§cErreur : Le type de votre ville n'a pas été reconnu"), Prefix.CITY, MessageType.ERROR, false);
            e.setCancelled(true);
            return;
        }

        if (cityEnemy_type == null) {
            MessagesManager.sendMessage(player, Component.text("§cErreur : Le type de la ville enemie n'a pas été reconnu"), Prefix.CITY, MessageType.ERROR, false);
            e.setCancelled(true);
            return;
        }

        if (pdcCityUUID.equals(city_uuid)) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas attaquer votre mascotte"), Prefix.CITY, MessageType.INFO, false);
            e.setCancelled(true);
            return;
        }

        if (cityEnemy_type.equals(CityType.PEACE)) {
            MessagesManager.sendMessage(player, Component.text("§cCette ville est en situation de §apaix"), Prefix.CITY, MessageType.INFO, false);
            e.setCancelled(true);
            return;
        }

        if (city_type.equals(CityType.PEACE)) {
            MessagesManager.sendMessage(player, Component.text("§cVotre ville est en situation de §apaix"), Prefix.CITY, MessageType.INFO, false);
            e.setCancelled(true);
            return;
        }

        if (cityEnemy.isImmune()) {
            MessagesManager.sendMessage(player, Component.text("§cCette mascotte est immunisée pour le moment"), Prefix.CITY, MessageType.INFO, false);
            e.setCancelled(true);
            return;
        }

        if (!city.isInWar() || !cityEnemy.isInWar() || !city.getWar().equals(cityEnemy.getWar())) {
            MessagesManager.sendMessage(player, Component.text("§cVous n'êtes pas en guerre contre " + cityEnemy.getName()), Prefix.CITY, MessageType.INFO, false);
            e.setCancelled(true);
            return;
        }

        War citiesWar = city.getWar();

        if (citiesWar.getPhase() != War.WarPhase.COMBAT) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez attaquer la mascotte que pendant la phase de combat"), Prefix.CITY, MessageType.INFO, false);
            e.setCancelled(true);
            return;
        }

        if (!citiesWar.getAttackers().contains(player.getUniqueId()) &&
                !citiesWar.getDefenders().contains(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas attaquer la mascotte car vous n'avez pas été séléctionné pour la guerre"), Prefix.CITY, MessageType.INFO, false);
            e.setCancelled(true);
            return;
        }

        if (!player.getEquipment().getItemInMainHand().getEnchantments().isEmpty()) {
            baseDamage = e.getDamage(EntityDamageByEntityEvent.DamageModifier.BASE);
            e.setDamage(baseDamage);
        }


        LivingEntity mob = (LivingEntity) damageEntity;
        City cityMob = MascotUtils.getCityFromEntity(mob.getUniqueId());

        try {
            if (MayorManager.getInstance().phaseMayor != 2) return;

            if (!PerkManager.hasPerk(cityMob.getMayor(), Perks.IRON_BLOOD.getId())) return;

            IronBloodPerk.spawnGolem(player, cityMob, mob);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            double newHealth = Math.floor(mob.getHealth());
            mob.setHealth(newHealth);
            if (newHealth <= 0) {
                mob.setHealth(0);
            }

            if (MascotRegenerationUtils.regenTasks.containsKey(damageEntity.getUniqueId())) {
                MascotRegenerationUtils.regenTasks.get(damageEntity.getUniqueId()).cancel();
                MascotRegenerationUtils.regenTasks.remove(damageEntity.getUniqueId());
            }

            MascotRegenerationUtils.startRegenCooldown(cityMob.getMascot());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        e.setCancelled(true);
    }

}
