package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Breeze;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Breezy extends DreamMob {

    public Breezy() {
        super("brezzy",
                "Breezy",
                EntityType.BREEZE,
                100.0,
                8.0,
                0.7,
                4.0,
                List.of(),
                Biome.THE_VOID
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        return null;
    }

    public EntitySnapshot createSnapshot() {
        World world = Bukkit.getWorld(DreamDimensionManager.DIMENSION_NAME);
        if (world == null) return null;
        Breeze breeze = world.createEntity(new Location(world, 0, 0, 0), Breeze.class);

        breeze.customName(Component.text(this.getName()));
        breeze.setCustomNameVisible(true);

        this.setAttributeIfPresent(breeze, Attribute.MAX_HEALTH, this.getHealth());
        breeze.setHealth(this.getHealth());
        this.setAttributeIfPresent(breeze, Attribute.ATTACK_DAMAGE, this.getDamage());
        this.setAttributeIfPresent(breeze, Attribute.MOVEMENT_SPEED, this.getSpeed());
        this.setAttributeIfPresent(breeze, Attribute.SCALE, this.getScale());

        breeze.addPotionEffect(new PotionEffect(
                PotionEffectType.INFESTED,
                Integer.MAX_VALUE,
                0,
                false,
                true
        ));
        breeze.setGlowing(true);
        breeze.setLootTable(null);

        breeze.getPersistentDataContainer().set(
                DreamMobsRegistry.mobKey,
                PersistentDataType.STRING,
                this.getId()
        );

        return breeze.createSnapshot();
    }
}