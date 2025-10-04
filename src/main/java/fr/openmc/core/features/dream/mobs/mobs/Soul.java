package fr.openmc.core.features.dream.mobs.mobs;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.items.DreamItemRegister;
import fr.openmc.core.features.dream.mobs.DreamLoot;
import fr.openmc.core.features.dream.mobs.DreamMob;
import fr.openmc.core.features.dream.mobs.DreamMobManager;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.SkullUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Soul extends DreamMob {

    public Soul() {
        super("soul",
                "Ame",
                EntityType.ARMOR_STAND,
                2,
                3.0,
                RandomUtils.randomBetween(0.8, 1),
                RandomUtils.randomBetween(0.7, 0.9),
                List.of(new DreamLoot(
                        DreamItemRegister.getByName("omc_dream:soul"),
                        0.70,
                        2,
                        3
                )),
                DreamBiome.SOUL_FOREST.getBiome()
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        World world = location.getWorld();
        if (world == null) return null;

        Vex vex = (Vex) world.spawnEntity(location, EntityType.VEX, CreatureSpawnEvent.SpawnReason.CUSTOM);
        vex.setSilent(true);
        vex.setInvisible(true);
        vex.setPersistent(true);

        vex.getEquipment().clear();

        this.setAttributeIfPresent(vex, Attribute.MAX_HEALTH, this.getHealth());
        vex.setHealth(this.getHealth());
        this.setAttributeIfPresent(vex, Attribute.ATTACK_DAMAGE, this.getDamage());
        this.setAttributeIfPresent(vex, Attribute.MOVEMENT_SPEED, this.getSpeed());
        this.setAttributeIfPresent(vex, Attribute.SCALE, this.getScale());

        vex.getPersistentDataContainer().set(
                DreamMobManager.mobKey,
                PersistentDataType.STRING,
                this.getId()
        );

        ArmorStand stand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        stand.customName(Component.text("§5§lÂme"));
        stand.setCustomNameVisible(true);
        stand.setInvisible(true);
        stand.setSmall(true);
        stand.setGravity(false);
        stand.setMarker(false);

        stand.getEquipment().setHelmet(SkullUtils.getCustomHead(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTc5YTkxMTg0NmJjY2YzNWM5ODM4ZjljMmQ5NjRmMjNjMzI1ODQ3ZTQ0ZDA3ZTU0NGFmZjdhMjA2YmY0NGI3MyJ9fX0=",
                "§6§lSoul"
        ));

        this.setAttributeIfPresent(stand, Attribute.MAX_HEALTH, this.getHealth());
        stand.setHealth(this.getHealth());

        stand.getPersistentDataContainer().set(
                DreamMobManager.mobKey,
                PersistentDataType.STRING,
                this.getId()
        );

        vex.addPassenger(stand);

        registerSoulLink(vex, stand);

        return vex;
    }

    private void registerSoulLink(Vex vex, ArmorStand stand) {
        Bukkit.getPluginManager().registerEvent(EntityDamageEvent.class, new Listener() {
        }, EventPriority.NORMAL, (listener, event) -> {
            if (!(event instanceof EntityDamageEvent dmgEvent)) return;
            Entity entity = dmgEvent.getEntity();

            if (entity.equals(vex) && stand.isValid() && stand instanceof LivingEntity armor) {
                double newHealth = Math.max(0, armor.getHealth() - dmgEvent.getFinalDamage());
                armor.setHealth(newHealth);
            } else if (entity.equals(stand) && vex.isValid()) {
                double newHealth = Math.max(0, vex.getHealth() - dmgEvent.getFinalDamage());
                vex.setHealth(newHealth);
            }
        }, OMCPlugin.getInstance());

        Bukkit.getPluginManager().registerEvent(EntityDeathEvent.class, new Listener() {
        }, EventPriority.NORMAL, (listener, event) -> {
            if (!(event instanceof EntityDeathEvent e)) return;

            Entity dead = e.getEntity();
            if (dead.equals(vex) && stand.isValid()) {
                stand.remove();
            } else if (dead.equals(stand) && vex.isValid()) {
                vex.remove();
            }
        }, OMCPlugin.getInstance());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!vex.isValid() || !stand.isValid()) {
                    if (vex.isValid()) vex.remove();
                    if (stand.isValid()) stand.remove();
                    cancel();
                }
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 40L);
    }
}