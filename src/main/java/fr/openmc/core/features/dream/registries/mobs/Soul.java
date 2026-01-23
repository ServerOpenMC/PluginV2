package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.models.registry.loottable.DreamLoot;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Soul extends DreamMob {

    public Soul() {
        super("soul",
                "Âme",
                EntityType.ARMOR_STAND,
                2,
                3L,
                RandomUtils.randomBetween(0.4, 0.5),
                RandomUtils.randomBetween(0.7, 0.9),
                null
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        World world = location.getWorld();
        if (world == null) return null;

        Vex vex = (Vex) world.spawnEntity(location, EntityType.VEX, CreatureSpawnEvent.SpawnReason.CUSTOM);

        vex.setSilent(true);
        vex.setInvisible(true);
        vex.setPersistent(false);

        vex.getEquipment().clear();

        this.setAttributeIfPresent(vex, Attribute.MAX_HEALTH, this.getHealth());
        vex.setHealth(this.getHealth());
        this.setAttributeIfPresent(vex, Attribute.MOVEMENT_SPEED, this.getSpeed());
        this.setAttributeIfPresent(vex, Attribute.SCALE, this.getScale());

        vex.getPersistentDataContainer().set(
                DreamMobsRegistry.mobKey,
                PersistentDataType.STRING,
                this.getId()
        );

        ArmorStand stand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        stand.customName(Component.text("§5§lÂme"));
        stand.setCustomNameVisible(true);
        stand.setInvisible(true);
        stand.setSmall(true);
        stand.setPersistent(false);
        stand.setGravity(false);
        stand.setMarker(false);

        stand.getEquipment().setHelmet(SkullUtils.getCustomHead(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTc5YTkxMTg0NmJjY2YzNWM5ODM4ZjljMmQ5NjRmMjNjMzI1ODQ3ZTQ0ZDA3ZTU0NGFmZjdhMjA2YmY0NGI3MyJ9fX0=",
                "§6§lSoul"
        ));

        this.setAttributeIfPresent(stand, Attribute.MAX_HEALTH, this.getHealth());
        stand.setHealth(this.getHealth());

        stand.getPersistentDataContainer().set(
                DreamMobsRegistry.mobKey,
                PersistentDataType.STRING,
                this.getId()
        );

        vex.addPassenger(stand);

        registerSoulLink(vex, stand);

        return vex;
    }

    private final List<DreamLoot> loots = List.of(new DreamLoot(
            DreamItemRegistry.getByName("omc_dream:soul"),
            0.70,
            1,
            2
    ));

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
                for (DreamLoot loot : loots) {
                    if (Math.random() >= loot.chance()) return;

                    int amount = loot.minAmount() + (int) (Math.random() * (loot.maxAmount() - loot.minAmount() + 1));
                    ItemStack drop = loot.item().getBest().asQuantity(amount);
                    dead.getWorld().dropItemNaturally(dead.getLocation(), drop);
                }
            } else if (dead.equals(stand) && vex.isValid()) {
                vex.remove();
                for (DreamLoot loot : loots) {
                    if (Math.random() >= loot.chance()) return;

                    int amount = loot.minAmount() + (int) (Math.random() * (loot.maxAmount() - loot.minAmount() + 1));
                    ItemStack drop = loot.item().getBest().asQuantity(amount);
                    dead.getWorld().dropItemNaturally(dead.getLocation(), drop);
                }
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