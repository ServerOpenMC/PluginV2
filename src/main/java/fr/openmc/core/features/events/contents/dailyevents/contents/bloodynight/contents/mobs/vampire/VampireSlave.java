package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.Zombie;

public class VampireSlave extends CustomMob<Zombie> {
    public VampireSlave(String id) {
        super(id,
                "Esclave du Vampire",
                Zombie.class,
                16,
                5,
                RandomUtils.randomBetween(0.1, 0.2)
        );
    }

    @Override
    public Zombie spawn(Location spawnLocation) {
        Zombie zombie = this.getPreBuildMob(spawnLocation);

        zombie.getEquipment().setHelmet(OMCRegistry.CUSTOM_ITEMS.VAMPIRE_HEAD.getBest());
        zombie.getEquipment().setHelmetDropChance(0);

        return zombie;
    }
}