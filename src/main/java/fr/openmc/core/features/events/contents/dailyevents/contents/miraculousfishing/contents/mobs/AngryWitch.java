package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftWitch;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Witch;

import java.util.List;

public class AngryWitch extends CustomMob<Witch> {
    public AngryWitch(String id) {
        super(id,
                "Sorcière énervée",
                Witch.class,
                100,
                67,
                RandomUtils.randomBetween(0.1, 0.1),
                List.of(
                        new ItemLoot(Material.EXPERIENCE_BOTTLE, 0.7, 1, 100),
                        new XpLoot(100, 150, 0.5)
                )
        );
    }

    @Override
    public Witch spawn(Location spawnLocation) {
        Witch witch = this.getPreBuildMob(spawnLocation);

        startPotionLaunch(witch);

        return witch;
    }

    public void startPotionLaunch(Witch witch) {
        Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(),task -> {
            if (witch.isDead() || !witch.isValid()) {
                task.cancel();
                return;
            }

            LivingEntity target = witch.getTarget();
            if (target == null) return;

            net.minecraft.world.entity.monster.Witch witchNMS = ((CraftWitch) witch).getHandle();
            net.minecraft.world.entity.LivingEntity targetNMS = ((CraftLivingEntity) target).getHandle();
            witchNMS.performRangedAttack(targetNMS, 0.1F);
        }, 0L, 20L * 2);
    }
}
