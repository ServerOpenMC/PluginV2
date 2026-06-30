package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Nautilus;
import org.bukkit.entity.Player;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Leviathan extends CustomMob<Nautilus> {
    public Leviathan(String id) {
        super(id,
                "Léviathan",
                Nautilus.class,
                40,
                20,
                RandomUtils.randomBetween(0.3, 0.5),
                List.of(
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.LEVIATHAN_HEAD,
                                0.50, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.FISHING_FURNITURE_BOX, 0.20, 1),
                        new XpLoot(30, 60, 1)
                )
        );
    }

    @Override
    public Nautilus spawn(Location spawnLocation) {
        Nautilus nautilus = this.getPreBuildMob(spawnLocation);

        spawnPassager(nautilus, spawnLocation);

        startDashAi(nautilus);

        return nautilus;
    }

    private void startDashAi(Nautilus nautilus) {
        Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), task -> {
            if (nautilus.isDead()) {
                task.cancel();
                return;
            }

            Optional<Player> target = nautilus.getLocation().getNearbyPlayers(16).stream()
                    .min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(nautilus.getLocation())));

            target.ifPresent(t -> triggerDash(nautilus, t));
        }, 20L, 60L);
    }

    private void triggerDash(Nautilus nautilus, LivingEntity target) {
        nautilus.setMemory(MemoryKey.ANGRY_AT, target.getUniqueId());
        nautilus.setMemory(MemoryKey.ATTACK_TARGET_COOLDOWN, null);
    }

    private void spawnPassager(Nautilus nautilus, Location spawnLocation) {
        Drowned drowned = spawnLocation.getWorld().spawn(spawnLocation, Drowned.class);
        if (ThreadLocalRandom.current().nextFloat() < 0.1f)
            drowned.setBaby();
        drowned.setShouldBurnInDay(false);
        drowned.setAggressive(true);
        drowned.getEquipment().setItemInMainHand(getDrownedTrident());
        drowned.getEquipment().setHelmet(OMCRegistry.CUSTOM_ITEMS.LEVIATHAN_HEAD.getBest());
        drowned.getEquipment().setHelmetDropChance(0f);

        AttributeInstance maxHealth = drowned.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null)
            maxHealth.setBaseValue(this.getHealth());

        drowned.setHealth(this.getHealth());

        AttributeInstance attackSpeed = drowned.getAttribute(Attribute.ATTACK_SPEED);
        if (attackSpeed != null)
            attackSpeed.setBaseValue(6);

        nautilus.addPassenger(drowned);
    }

    private ItemStack getDrownedTrident() {
        ItemStack trident = new ItemStack(Material.TRIDENT);

        if (ThreadLocalRandom.current().nextBoolean())
            trident.addEnchantment(Enchantment.LOYALTY, 1);
        if (ThreadLocalRandom.current().nextBoolean())
            trident.addEnchantment(Enchantment.CHANNELING, 1);

        return trident;
    }
}
