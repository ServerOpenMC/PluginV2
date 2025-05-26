package fr.openmc.core.features.city.mascots;

import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import fr.openmc.core.features.city.menu.mascots.MascotMenu;
import fr.openmc.core.features.city.menu.mascots.MascotsDeadMenu;
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

public class MascotsListener implements Listener {

    public static final Map<UUID, BukkitRunnable> regenTasks = new HashMap<>();
    private final Map<UUID, BukkitRunnable> cooldownTasks = new HashMap<>();
    public static List<String> movingMascots = new ArrayList<>();

    @SneakyThrows
    public MascotsListener() {
        for (Mascot mascot : MascotsManager.mascots) {
            mascotsRegeneration(mascot.getMascotUUID());
        }
    }

    @EventHandler
    void onMascotDamageCaused(EntityDamageEvent e){
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) return;

        if (!MascotUtils.isMascot(entity)) return;

        if (e.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
            e.setCancelled(true);
        }

        City city = MascotUtils.getCityFromMascot(entity.getUniqueId());
        if (city == null) return;
        LivingEntity mob = (LivingEntity) entity;

        double newHealth = Math.floor(mob.getHealth());
        mob.setHealth(newHealth);
        double maxHealth = mob.getMaxHealth();

        Mascot mascot = city.getMascot();
        if (mascot == null) return;

        if (!mascot.isAlive()) {
            mob.setCustomName("§lMascotte en attente de §csoins");
        } else {
            mob.setCustomName("§l" + city.getName() + " §c" + newHealth + "/" + maxHealth + "❤");
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
        String mascotsUUID = data.get(MascotsManager.mascotsKey, PersistentDataType.STRING);

        if (mascotsUUID == null) return;

        City city = CityManager.getPlayerCity(player.getUniqueId());
        City cityEnemy = MascotUtils.getCityFromMascot(damageEntity.getUniqueId());
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

        if (mascotsUUID.equals(city_uuid)) {
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

        if (!player.getEquipment().getItemInMainHand().getEnchantments().isEmpty()) {
            baseDamage = e.getDamage(EntityDamageByEntityEvent.DamageModifier.BASE);
            e.setDamage(baseDamage);
        }

        LivingEntity mob = (LivingEntity) damageEntity;
        try {
            double newHealth = Math.floor(mob.getHealth());
            mob.setHealth(newHealth);
            double maxHealth = mob.getMaxHealth();
            mob.setCustomName("§l" + cityEnemy.getName() + " §c" + newHealth + "/" + maxHealth + "❤");

            if (MayorManager.getInstance().phaseMayor != 2) return;

            if (!PerkManager.hasPerk(MascotUtils.getCityFromMascot(mob.getUniqueId()).getMayor(), Perks.IRON_BLOOD.getId()))
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
                            return enemyCity != null && !enemyCity.getUUID().equals(MascotUtils.getCityFromMascot(mob.getUniqueId()).getUUID());
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


            if (regenTasks.containsKey(damageEntity.getUniqueId())) {
                regenTasks.get(damageEntity.getUniqueId()).cancel();
                regenTasks.remove(damageEntity.getUniqueId());
            }

            startRegenCooldown(damageEntity.getUniqueId());
            CityCommands.startBalanceCooldown(city_uuid);

            if (newHealth <= 0) {
                mob.setHealth(0);
            }

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
                        return enemyCity != null && !enemyCity.getUUID().equals(MascotUtils.getCityFromMascot(mascotUUID).getUUID());
                    })
                    .collect(Collectors.toList());

            if (nearbyEnemies.isEmpty() && golem.getTarget() == null) {
                golem.remove();
            } else {
                Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> scheduleGolemDespawn(golem, mascotUUID), 200L);
            }
        }, delayInitial);
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
            if (!city.getMascot().isAlive()) {
                new MascotsDeadMenu(player, city_uuid).open();
            } else {
                new MascotMenu(player, clickEntity).open();
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("§cCette mascotte ne vous appartient pas"), Prefix.CITY, MessageType.ERROR, false);
        }
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

        MascotUtils.changeMascotImmunity(city_uuid, true);
        MascotUtils.changeMascotState(city_uuid, false);

        entity.setCustomName("§lMascotte en attente de §csoins");
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

    private void startRegenCooldown(UUID mascotsUUID) {
        if (cooldownTasks.containsKey(mascotsUUID)) {
            cooldownTasks.get(mascotsUUID).cancel();
        }

        BukkitRunnable cooldownTask = new BukkitRunnable() {
            @Override
            public void run() {
                mascotsRegeneration(mascotsUUID);
                cooldownTasks.remove(mascotsUUID);
            }
        };

        cooldownTasks.put(mascotsUUID, cooldownTask);
        cooldownTask.runTaskLater(OMCPlugin.getInstance(), 10 * 60 * 20L);
    }

    public static void mascotsRegeneration(UUID mascotsUUID) {
        if (regenTasks.containsKey(mascotsUUID)) return;

        Mascot mascot = MascotUtils.getMascotByUUID(mascotsUUID);
        if (mascot == null) return;

        Entity mob = MascotUtils.loadMascot(mascot);
        if (mob == null) return;

        PersistentDataContainer data = mob.getPersistentDataContainer();
        if (!data.has(MascotsManager.mascotsKey, PersistentDataType.STRING)) return;

        String city_uuid = data.get(MascotsManager.mascotsKey, PersistentDataType.STRING);

        if (!MascotUtils.mascotsContains(city_uuid)) {
            regenTasks.remove(mascotsUUID);
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
                LivingEntity mascots = MascotUtils.loadMascot(mascot);
                if (mascots == null || mascots.isDead()) {
                    regenTasks.remove(mascotsUUID);
                    this.cancel();
                    return;
                }

                if (mascots.getHealth() >= mascots.getMaxHealth()) {
                    mascots.setCustomName("§l" + MascotUtils.getCityFromMascot(mascotsUUID).getName() + " §c" + mascots.getHealth() + "/" + mascots.getMaxHealth() + "❤");
                    regenTasks.remove(mascotsUUID);
                    this.cancel();
                    return;
                }

                double newHealth = Math.min(mascots.getHealth() + 1, mascots.getMaxHealth());
                mascots.setHealth(newHealth);
                mascots.setCustomName("§l" + MascotUtils.getCityFromMascot(mascotsUUID).getName() + " §c" + mascots.getHealth() + "/" + mascots.getMaxHealth() + "❤");
            }
        };

        regenTasks.put(mascotsUUID, task);
        task.runTaskTimer(OMCPlugin.getInstance(), 0L, 60L);
    }
}
