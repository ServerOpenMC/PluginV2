package fr.openmc.core.registry.mobs.options;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface MobBossbarImpl {
    Map<UUID, BossBar> ACTIVE_BOSS_BARS = new ConcurrentHashMap<>();

    default Component getBossBarName(LivingEntity entity) {
        return entity.customName() != null
                ? entity.customName()
                : Component.text(entity.getName());
    }

    BossBar.Color getBossBarColor();

    BossBar.Overlay getBossBarOverlay();

    double getBossBarViewRadius();

    default BossBar createBossBar(LivingEntity entity) {
        BossBar bossBar = BossBar.bossBar(
                getBossBarName(entity),
                getHealthProgress(entity),
                getBossBarColor(),
                getBossBarOverlay()
        );

        ACTIVE_BOSS_BARS.put(entity.getUniqueId(), bossBar);

        return bossBar;
    }

    default BossBar getBossBar(LivingEntity entity) {
        return ACTIVE_BOSS_BARS.get(entity.getUniqueId());
    }

    default BossBar getOrCreateBossBar(LivingEntity entity) {
        BossBar bossBar = getBossBar(entity);

        if (bossBar != null) {
            return bossBar;
        }

        return createBossBar(entity);
    }

    default void updateBossBar(LivingEntity entity) {
        BossBar bossBar = getOrCreateBossBar(entity);

        bossBar.name(getBossBarName(entity));
        bossBar.progress(getHealthProgress(entity));
    }

    default void updateBossBarViewers(LivingEntity entity) {
        BossBar bossBar = getOrCreateBossBar(entity);

        double radius = getBossBarViewRadius();
        double radiusSquared = radius * radius;

        Collection<Player> players = entity.getWorld().getPlayers();

        for (Player player : players) {
            boolean shouldSee =
                    player.getLocation().distanceSquared(entity.getLocation())
                            <= radiusSquared;

            if (shouldSee) {
                player.showBossBar(bossBar);
            } else {
                player.hideBossBar(bossBar);
            }
        }
    }

    default void removeBossBar(LivingEntity entity) {
        BossBar bossBar = ACTIVE_BOSS_BARS.remove(entity.getUniqueId());

        if (bossBar == null) {
            return;
        }

        for (Player player : entity.getWorld().getPlayers()) {
            player.hideBossBar(bossBar);
        }
    }

    private float getHealthProgress(LivingEntity entity) {
        AttributeInstance maxHealthAttribute = entity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttribute == null) return 0;

        if (maxHealthAttribute.getValue() <= 0) {
            return 0.0F;
        }

        double progress = entity.getHealth() / maxHealthAttribute.getValue();

        return (float) Math.clamp(progress, 0.0, 1.0);
    }
}
