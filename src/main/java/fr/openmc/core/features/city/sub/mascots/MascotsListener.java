package fr.openmc.core.features.city.sub.mascots;

import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.menu.MascotMenu;
import fr.openmc.core.features.city.sub.mascots.menu.MascotsDeadMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import io.papermc.paper.event.entity.EntityMoveEvent;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
