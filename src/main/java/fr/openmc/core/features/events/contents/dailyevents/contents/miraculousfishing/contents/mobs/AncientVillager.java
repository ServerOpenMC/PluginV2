package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs;

import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;

import java.util.List;

public class AncientVillager extends CustomMob<ZombieVillager> {
    public AncientVillager(String id) {
        super(id,
                TranslationManager.translation("feature.dailyevents.miraculousfishing.mob.ancient_villager"),
                ZombieVillager.class,
                100,
                67,
                RandomUtils.randomBetween(0.1, 0.1),
                List.of(
                        new XpLoot(100, 150, 0.5)
                )
        );
    }

    @Override
    public ZombieVillager spawn(Location spawnLocation) {
        ZombieVillager zombieVillager = this.getPreBuildMob(spawnLocation);

        zombieVillager.setVillagerProfession(Villager.Profession.FISHERMAN);
        zombieVillager.setVillagerType(Villager.Type.SWAMP);

        return zombieVillager;
    }
}
