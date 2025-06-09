package fr.openmc.core.features.city.sub.mascots;

import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.mascots.menu.MascotMenu;
import fr.openmc.core.features.city.sub.mascots.menu.MascotsDeadMenu;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import io.papermc.paper.event.entity.EntityMoveEvent;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

import static fr.openmc.core.features.city.commands.CityCommands.balanceCooldownTasks;
import static fr.openmc.core.features.city.sub.mascots.MascotsManager.DEAD_MASCOT_NAME;

public class MascotsListener implements Listener {

    public static final Map<UUID, BukkitRunnable> regenTasks = new HashMap<>();
    private final Map<UUID, BukkitRunnable> cooldownTasks = new HashMap<>();
    public static List<String> movingMascots = new ArrayList<>();

    @SneakyThrows
    public MascotsListener() {
        for (Mascot mascot : MascotsManager.mascotsByCityUUID.values()) {
            mascotsRegeneration(mascot);
        }
    }

    @EventHandler
    void onMascotDamageCaused(EntityDamageEvent e){
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

    private final Map<City, Long> perkIronBloodCooldown = new HashMap<>();
    private static final long COOLDOWN_TIME = 3 * 60 * 1000L;  // 3 minutes


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

        if (cityEnemy.getMascot().isImmunity()) {
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

            if (!PerkManager.hasPerk(cityMob.getMayor(), Perks.IRON_BLOOD.getId()))
                return;
            long currentTime = System.currentTimeMillis();
            if (perkIronBloodCooldown.containsKey(city) && currentTime - perkIronBloodCooldown.get(city) < COOLDOWN_TIME) {
                return;
            }
            perkIronBloodCooldown.put(city, currentTime);
            org.bukkit.Location location = mob.getLocation().clone();
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
                        .filter(ent -> ent instanceof Player)
                        .map(ent -> (Player) ent)
                        .filter(nearbyPlayer -> {
                            City enemyCity = CityManager.getPlayerCity(nearbyPlayer.getUniqueId());
                            return enemyCity != null && !enemyCity.getUUID().equals(cityMob.getUUID());
                        })
                        .collect(Collectors.toList());

                if (!nearbyEnemies.isEmpty()) {
                    Player target = nearbyEnemies.get(0);
                    golem.setAI(true);
                    golem.setTarget(target);
                    org.bukkit.util.Vector direction = target.getLocation().toVector().subtract(golem.getLocation().toVector()).normalize();
                    golem.setVelocity(direction.multiply(0.5));
                } else {
                    golem.setAI(false);
                    golem.setTarget(null);
                }
            }, 0L, 20L);
            scheduleGolemDespawn(golem, mob.getUniqueId());

            MessagesManager.sendMessage(player, Component.text("§8§o*tremblement* Quelque chose semble arriver..."), Prefix.MAYOR, MessageType.INFO, false);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            double newHealth = Math.floor(mob.getHealth());
            mob.setHealth(newHealth);
            if (newHealth <= 0) {
                mob.setHealth(0);
            }

            if (regenTasks.containsKey(damageEntity.getUniqueId())) {
                regenTasks.get(damageEntity.getUniqueId()).cancel();
                regenTasks.remove(damageEntity.getUniqueId());
            }

            startRegenCooldown(cityMob.getMascot());
            startBalanceCooldown(city_uuid);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        e.setCancelled(true);
    }

    private void scheduleGolemDespawn(IronGolem golem, UUID mascotUUID) {
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
                    .collect(Collectors.toList());

            if (nearbyEnemies.isEmpty() && golem.getTarget() == null) {
                golem.remove();
            } else {
                Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> scheduleGolemDespawn(golem, mascotUUID), 200L);
            }
        }, delayInitial);
    }

    public static void startBalanceCooldown(String city_uuid) {
        if (balanceCooldownTasks.containsKey(city_uuid)) {
            balanceCooldownTasks.get(city_uuid).cancel();
        }

        BukkitRunnable cooldownTask = new BukkitRunnable() {
            @Override
            public void run() {
                balanceCooldownTasks.remove(city_uuid);
            }
        };

        balanceCooldownTasks.put(city_uuid, cooldownTask);
        cooldownTask.runTaskLater(OMCPlugin.getInstance(), 30 * 60 * 20L);
    }

    @EventHandler
    public void onBlockPlace(CustomBlockPlaceEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();

        Collection<Entity> nearbyEntities = loc.getWorld().getNearbyEntities(loc, 1.5, 1.5, 1.5);

        for (Entity entity : nearbyEntities) {
            if (!MascotUtils.isMascot(entity)) return;

            event.setCancelled(true);
            return;
        }
    }

    @SneakyThrows
    @EventHandler
    void onInteractWithMascots(PlayerInteractEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;

        Player player = e.getPlayer();
        Entity clickEntity = e.getRightClicked();

        if (!MascotUtils.isMascot(clickEntity)) return;

        PersistentDataContainer data = clickEntity.getPersistentDataContainer();
        String mascotsUUID = data.get(MascotsManager.mascotsKey, PersistentDataType.STRING);
        if (mascotsUUID == null) return;

        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        String city_uuid = city.getUUID();
        if (mascotsUUID.equals(city_uuid)) {
            Mascot mascot = city.getMascot();
            if (!mascot.isAlive()) {
                new MascotsDeadMenu(player, city_uuid).open();
            } else {
                new MascotMenu(player, mascot).open();
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("§cCette mascotte ne vous appartient pas"), Prefix.CITY, MessageType.ERROR, false);
        }
    }

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

        int level = mascot.getLevel();

        mascot.setImmunity(true);
        mascot.setAlive(false);

        entity.customName(Component.text(DEAD_MASCOT_NAME));
        entity.setGlowing(true);
        e.setCancelled(true);

        for (UUID townMember : city.getMembers()) {
            if (!(Bukkit.getEntity(townMember) instanceof Player player)) continue;
            for (PotionEffect potionEffect : MascotsLevels.valueOf("level" + level).getBonus()) {
                player.removePotionEffect(potionEffect.getType());
            }
            MascotsManager.giveMascotsEffect(townMember);
        }

        if (killer == null) return;

        City cityEnemy = CityManager.getPlayerCity(killer.getUniqueId());

        if (cityEnemy == null) return;

        cityEnemy.updatePowerPoints(level);
        city.updatePowerPoints(-level);

        cityEnemy.updateBalance(0.15 * city.getBalance() / 100);
        city.updateBalance(-(0.15 * city.getBalance() / 100));
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!MascotUtils.isMascot(event.getEntity())) return;

        event.setCancelled(true);
    }

    @EventHandler
    void onLightningStrike(LightningStrikeEvent e) {
        Location strikeLocation = e.getLightning().getLocation();

        for (Entity entity : strikeLocation.getWorld().getNearbyEntities(strikeLocation, 3, 3, 3)) {
            if (!(entity instanceof LivingEntity)) continue;

            if (!MascotUtils.isMascot(entity)) continue;

            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    void onPistonExtend(BlockPistonExtendEvent e) {
        Location pistonHeadLocation = e.getBlock().getRelative(e.getDirection()).getLocation();
        for (Entity entity : pistonHeadLocation.getWorld().getNearbyEntities(pistonHeadLocation, 0.5, 0.5, 0.5)) {
            if (!(entity instanceof LivingEntity)) continue;
            if (!MascotUtils.isMascot(entity)) continue;

            e.setCancelled(true);
            return;
        }
        for (Block block : e.getBlocks()) {
            Location futureLocation = block.getRelative(e.getDirection()).getLocation();
            for (Entity entity : block.getWorld().getNearbyEntities(futureLocation, 0.5, 0.5, 0.5)) {
                if (!(entity instanceof LivingEntity)) continue;
                if (!(MascotUtils.isMascot(entity))) continue;

                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    void onTransform(EntityTransformEvent event) {
        Entity entity = event.getEntity();
        if (!MascotUtils.isMascot(entity)) return;

        event.setCancelled(true);
    }

    @EventHandler
    void onPortal(EntityPortalEvent event) {
        Entity entity = event.getEntity();
        if (!MascotUtils.isMascot(entity)) return;

        event.setCancelled(true);
    }

    @EventHandler
    void onFire(EntityCombustEvent e) {
        Entity entity = e.getEntity();
        if (!MascotUtils.isMascot(entity)) return;

        e.setCancelled(true);
    }

    @EventHandler
    void onPigMount(EntityMountEvent e) {
        Entity entity = e.getMount();
        if (!MascotUtils.isMascot(entity)) return;

        e.setCancelled(true);
    }

    @EventHandler
    void onMove(EntityMoveEvent e) {
        Entity entity = e.getEntity();
        if (!MascotUtils.isMascot(entity)) return;

        e.setCancelled(true);
    }

    @EventHandler
    void onAxolotlBucket(PlayerBucketEntityEvent e) {
        Entity entity = e.getEntity();
        if (!MascotUtils.isMascot(entity)) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onMilkDrink(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType() == Material.MILK_BUCKET) {
            MascotsManager.giveMascotsEffect(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        for (MascotsLevels levels : MascotsLevels.values()){
            for (PotionEffect effect : levels.getMalus()){
                player.removePotionEffect(effect.getType());
            }
        }

        MascotsManager.giveMascotsEffect(player.getUniqueId());
    }

    private void startRegenCooldown(Mascot mascots) {
        UUID mascotsUUID = mascots.getMascotUUID();
        if (cooldownTasks.containsKey(mascotsUUID)) {
            cooldownTasks.get(mascotsUUID).cancel();
        }

        BukkitRunnable cooldownTask = new BukkitRunnable() {
            @Override
            public void run() {
                mascotsRegeneration(mascots);
                cooldownTasks.remove(mascotsUUID);
            }
        };

        cooldownTasks.put(mascotsUUID, cooldownTask);
        cooldownTask.runTaskLater(OMCPlugin.getInstance(), 10 * 60 * 20L);
    }

    public static void mascotsRegeneration(Mascot mascot) {
        if (regenTasks.containsKey(mascot.getMascotUUID())) return;

        LivingEntity mob = (LivingEntity) mascot.getEntity();
        PersistentDataContainer data = mob.getPersistentDataContainer();
        if (!data.has(MascotsManager.mascotsKey, PersistentDataType.STRING)) return;

        if (mascot == null) {
            regenTasks.remove(mascot.getMascotUUID());
            return;
        }

        if (!mascot.isAlive()) return;

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (mascot==null){
                    this.cancel();
                    return;
                }
                LivingEntity mascots = (LivingEntity) mascot.getEntity();
                if (mascots == null || mascots.isDead()) {
                    regenTasks.remove(mascot.getMascotUUID());
                    this.cancel();
                    return;
                }


                if (mascots.getHealth() >= mascots.getMaxHealth()) {

                    mascots.customName(Component.text(MascotsManager.PLACEHOLDER_MASCOT_NAME.formatted(
                            mascot.getCity().getName(),
                            Math.floor(mascots.getHealth()),
                            mascots.getMaxHealth()
                    )));
                    regenTasks.remove(mascot.getMascotUUID());
                    this.cancel();
                    return;
                }

                double newHealth = Math.min(mascots.getHealth() + 1, mascots.getMaxHealth());
                mascots.setHealth(newHealth);
                mascots.customName(Component.text(MascotsManager.PLACEHOLDER_MASCOT_NAME.formatted(
                        mascot.getCity().getName(),
                        Math.floor(mascots.getHealth()),
                        mascots.getMaxHealth()
                )));
            }
        };

        regenTasks.put(mascot.getMascotUUID(), task);
        task.runTaskTimer(OMCPlugin.getInstance(), 0L, 60L);
    }
}
