package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs;

import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
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
        Witch witch = spawnLocation.getWorld().spawn(spawnLocation, Witch.class);

        witch.setPotionUseTimeLeft(20);

        return witch;
    }
}
