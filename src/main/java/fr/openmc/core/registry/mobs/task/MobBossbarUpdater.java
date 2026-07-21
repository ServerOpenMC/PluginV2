package fr.openmc.core.registry.mobs.task;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.registry.mobs.CustomMobRegistry;
import fr.openmc.core.registry.mobs.options.MobBossbarImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.UUID;

public class MobBossbarUpdater extends BukkitRunnable {

    @Override
    public void run() {
        Iterator<UUID> iterator = CustomMobRegistry.HAS_BOSSBAR.iterator();

        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            Entity entity = Bukkit.getEntity(uuid);

            if (!(entity instanceof LivingEntity livingEntity)) return;
            CustomMob<?> customMob = OMCRegistry.CUSTOM_MOBS.getMob(entity);
            if (customMob == null) return;
            if (!(customMob instanceof MobBossbarImpl bossBarMob)) return;

            if (!livingEntity.isValid() || livingEntity.isDead()) {
                bossBarMob.removeBossBar(livingEntity);
                iterator.remove();
                continue;
            }

            bossBarMob.updateBossBarViewers(livingEntity);
        }
    }
}
