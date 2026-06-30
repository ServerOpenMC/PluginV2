package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.registry.mobs.CustomMobAttribute;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.Guardian;
import org.bukkit.event.Listener;

import java.util.List;

public class GiantElderGuardian extends CustomMob<ElderGuardian> implements Listener {
    public GiantElderGuardian(String id) {
        super(id,
                "Géant Elder Gardian",
                ElderGuardian.class,
                100,
                8,
                List.of(
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.ANCIENT_FISHER_CHESTPLATE, 0.3, 1, 1),
                        new XpLoot(100, 120, 1)
                ),
                new CustomMobAttribute(Attribute.SCALE, 7)
        );
    }

    @Override
    public ElderGuardian spawn(Location spawnLocation) {
        ElderGuardian elderGuardian = this.getPreBuildMob(spawnLocation);

        for (int i = 0; i < 5; i++) {
            spawnLocation.getWorld().spawn(spawnLocation, Guardian.class);
        }

        return elderGuardian;
    }
}
