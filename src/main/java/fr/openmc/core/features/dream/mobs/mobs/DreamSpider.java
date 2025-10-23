package fr.openmc.core.features.dream.mobs.mobs;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.items.DreamItemRegister;
import fr.openmc.core.features.dream.mobs.DreamLoot;
import fr.openmc.core.features.dream.mobs.DreamMob;
import fr.openmc.core.features.dream.mobs.DreamMobManager;
import fr.openmc.core.utils.RandomUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class DreamSpider extends DreamMob {

    public DreamSpider() {
        super("dream_spider",
                "Arraignée Infestée",
                EntityType.SPIDER,
                8.0,
                3.0,
                RandomUtils.randomBetween(0.6, 0.8),
                RandomUtils.randomBetween(1.5, 2.0),
                List.of(new DreamLoot(
                        DreamItemRegister.getByName("omc_dream:corrupted_string"),
                        0.80,
                        1,
                        3
                )),
                DreamBiome.SCULK_PLAINS.getBiome()
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        Spider spider = (Spider) location.getWorld().spawnEntity(location, this.getType(), CreatureSpawnEvent.SpawnReason.CUSTOM);

        spider.customName(Component.text(this.getName()));
        spider.setCustomNameVisible(true);

        this.setAttributeIfPresent(spider, Attribute.MAX_HEALTH, this.getHealth());
        spider.setHealth(this.getHealth());
        this.setAttributeIfPresent(spider, Attribute.ATTACK_DAMAGE, this.getDamage());
        this.setAttributeIfPresent(spider, Attribute.MOVEMENT_SPEED, this.getSpeed());
        this.setAttributeIfPresent(spider, Attribute.SCALE, this.getScale());

        spider.setPersistent(true);

        spider.getPersistentDataContainer().set(
                DreamMobManager.mobKey,
                PersistentDataType.STRING,
                this.getId()
        );

        return spider;
    }
}