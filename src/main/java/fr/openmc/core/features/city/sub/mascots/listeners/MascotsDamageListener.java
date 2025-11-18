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

public class MascotsDamageListener implements Listener {
    private static final Set<EntityDamageEvent.DamageCause> BLOCKED_CAUSES = Set.of(
            EntityDamageEvent.DamageCause.SUFFOCATION,
            EntityDamageEvent.DamageCause.FALLING_BLOCK,
            EntityDamageEvent.DamageCause.LIGHTNING,
            EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
            EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
            EntityDamageEvent.DamageCause.FIRE_TICK
    );

    @EventHandler
    void onMascotDamageCaused(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof LivingEntity entity)) return;

        if (entity instanceof Player) return;

        if (!MascotUtils.canBeAMascot(entity)) return;

        EntityDamageEvent.DamageCause cause = e.getCause();

        if (BLOCKED_CAUSES.contains(cause)) {
            e.setCancelled(true);
            return;
        }

        City city = MascotUtils.getCityFromEntity(entity.getUniqueId());
        if (city == null) return;

        Mascot mascot = city.getMascot();
        if (mascot == null) return;

        // on return pour eviter d'actualiser 2 fois la vie
        if (city.isInWar()) return;

        MascotUtils.updateDisplayName(entity, mascot, e.getFinalDamage());
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
        String pdcCityData = data.get(MascotsManager.mascotsKey, PersistentDataType.STRING);
        if (pdcCityData == null) return;
        UUID pdcCityUUID = UUID.fromString(pdcCityData);

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
	        MessagesManager.sendMessage(player, Component.text("§cErreur : La ville ennemie n'a pas été reconnue"), Prefix.CITY, MessageType.ERROR, false);
            e.setCancelled(true);
            return;
        }
        UUID cityUUID = city.getUniqueId();

        CityType cityType = city.getType();
        CityType cityEnemyType = cityEnemy.getType();

        if (cityType == null) {
	        MessagesManager.sendMessage(player, Component.text("§cErreur : Le type de votre ville n'a pas été reconnue"), Prefix.CITY, MessageType.ERROR, false);
            e.setCancelled(true);
            return;
        }

        if (cityEnemyType == null) {
	        MessagesManager.sendMessage(player, Component.text("§cErreur : Le type de la ville ennemie n'a pas été reconnue"), Prefix.CITY, MessageType.ERROR, false);
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
	        MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas attaquer la mascotte car vous n'avez pas été sélectionné pour la guerre"), Prefix.CITY, MessageType.INFO, false);
            e.setCancelled(true);
            return;
        }

        LivingEntity mob = (LivingEntity) damageEntity;
        City cityMob = MascotUtils.getCityFromEntity(mob.getUniqueId());

        MascotUtils.updateDisplayName(mob, cityMob.getMascot(), e.getFinalDamage());

        try {
            if (MayorManager.phaseMayor != 2) return;

            if (!PerkManager.hasPerk(cityMob.getMayor(), Perks.IRON_BLOOD.getId())) return;

            IronBloodPerk.spawnGolem(player, cityMob, mob);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
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
