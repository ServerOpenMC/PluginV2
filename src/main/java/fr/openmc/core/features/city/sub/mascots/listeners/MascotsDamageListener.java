package fr.openmc.core.features.city.sub.mascots.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mascots.utils.MascotRegenerationUtils;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.sub.mayor.perks.basic.IronBloodPerk;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;
import java.util.UUID;

import static fr.openmc.core.features.city.sub.mascots.MascotsManager.DEAD_MASCOT_NAME;

public class MascotsDamageListener implements Listener {
    private static final Set<EntityDamageEvent.DamageCause> BLOCKED_CAUSES = Set.of(
            EntityDamageEvent.DamageCause.SUFFOCATION,
            EntityDamageEvent.DamageCause.FALLING_BLOCK,
            EntityDamageEvent.DamageCause.LIGHTNING,
            EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
            EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
            EntityDamageEvent.DamageCause.FIRE_TICK,
            EntityDamageEvent.DamageCause.ENTITY_ATTACK,
            EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
    );

    @EventHandler
    void onMascotDamageCaused(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (!(entity instanceof Player mob)) return;

        if (!MascotUtils.canBeAMascot(entity)) return;

        EntityDamageEvent.DamageCause cause = e.getCause();

        if (BLOCKED_CAUSES.contains(cause)) {
            e.setCancelled(true);
            return;
        }

        City city = MascotUtils.getCityFromEntity(entity.getUniqueId());
        if (city == null) return;

        double newHealth = Math.floor(mob.getHealth());
        mob.setHealth(newHealth);
        double maxHealth = mob.getAttribute(Attribute.MAX_HEALTH).getValue();

        Mascot mascot = city.getMascot();
        if (mascot == null) return;

        double healthAfterDamage = mob.getHealth() - e.getFinalDamage();
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

    @EventHandler
    void onMascotTakeDamage(EntityDamageByEntityEvent e) {
        Entity damageEntity = e.getEntity();
        Entity damager = e.getDamager();

        if (!MascotUtils.canBeAMascot(damageEntity)) return;

        if (!(damager instanceof Player player)) {
            e.setCancelled(true);
            return;
        }

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
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            e.setCancelled(true);
            return;
        }

        if (cityEnemy == null) {
            MessagesManager.sendMessage(player, Component.text("§cErreur : La ville enemie n'a pas été reconnu"), Prefix.CITY, MessageType.ERROR, false);
            e.setCancelled(true);
            return;
        }
        UUID cityUUID = city.getUniqueId();

        CityType cityType = city.getType();
        CityType cityEnemyType = cityEnemy.getType();

        if (cityType == null) {
            MessagesManager.sendMessage(player, Component.text("§cErreur : Le type de votre ville n'a pas été reconnu"), Prefix.CITY, MessageType.ERROR, false);
            e.setCancelled(true);
            return;
        }

        if (cityEnemyType == null) {
            MessagesManager.sendMessage(player, Component.text("§cErreur : Le type de la ville enemie n'a pas été reconnu"), Prefix.CITY, MessageType.ERROR, false);
            e.setCancelled(true);
            return;
        }

        if (pdcCityUUID.equals(cityUUID)) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas attaquer votre mascotte"), Prefix.CITY, MessageType.INFO, false);
            e.setCancelled(true);
            return;
        }

        if (cityEnemyType.equals(CityType.PEACE)) {
            MessagesManager.sendMessage(player, Component.text("§cCette ville est en situation de §apaix"), Prefix.CITY, MessageType.INFO, false);
            e.setCancelled(true);
            return;
        }

        if (cityType.equals(CityType.PEACE)) {
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


        LivingEntity mob = (LivingEntity) damageEntity;
        City cityMob = MascotUtils.getCityFromEntity(mob.getUniqueId());

        double newHealth = Math.floor(mob.getHealth());

        mob.setHealth(newHealth);
        if (newHealth <= 0) {
            mob.setHealth(0);
        }

        mob.customName(Component.text(MascotsManager.PLACEHOLDER_MASCOT_NAME.formatted(
                cityMob.getName(),
                mob.getHealth() - e.getFinalDamage(),
                mob.getAttribute(Attribute.MAX_HEALTH).getValue()
        )));

        try {
            if (MayorManager.phaseMayor != 2) return;

            if (!PerkManager.hasPerk(cityMob.getMayor(), Perks.IRON_BLOOD.getId())) return;

            IronBloodPerk.spawnGolem(player, cityMob, mob);
        } catch (Exception exception) {
            exception.printStackTrace();
        }


        if (MascotRegenerationUtils.regenTasks.containsKey(damageEntity.getUniqueId())) {
            MascotRegenerationUtils.regenTasks.get(damageEntity.getUniqueId()).cancel();
            MascotRegenerationUtils.regenTasks.remove(damageEntity.getUniqueId());
        }

        MascotRegenerationUtils.startRegenCooldown(cityMob.getMascot());

    }

    @EventHandler
    public void onMobTargetMascot(EntityTargetLivingEntityEvent event) {
        LivingEntity target = event.getTarget();

        if (target == null) return;
        if (!MascotUtils.canBeAMascot(target)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onMascotTarget(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getEntity();

        if (MascotUtils.canBeAMascot(entity)) {
            event.setCancelled(true);
        }
    }
}
