package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.RandomUtils;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Mannequin;

import java.util.List;
import java.util.UUID;

public class PoissonSteve extends CustomMob<Mannequin> {
    public PoissonSteve(String id) {
        super(id,
                "Le Poisson Steve",
                Mannequin.class,
                50,
                67,
                RandomUtils.randomBetween(0.1, 0.1),
                List.of(
                        new ItemLoot(Material.TROPICAL_FISH, Material.TROPICAL_FISH,
                                1, 10, 20),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.POISSON_STEVE_HEAD, OMCRegistry.CUSTOM_ITEMS.POISSON_STEVE_HEAD,
                                0.5, 1, 1)
                )
        );
    }

    @Override
    public Mannequin spawn(Location spawnLocation) {
        Mannequin mannequin = spawnLocation.getWorld().spawn(spawnLocation, Mannequin.class);

        mannequin.setProfile(ResolvableProfile.resolvableProfile()
                .uuid(UUID.fromString("d715d08e-c54c-45e6-b6d0-fe6caf2716a7"))
                .build());
        mannequin.getEquipment().setHelmet(OMCRegistry.CUSTOM_ITEMS.POISSON_STEVE_HEAD.getBest());
        mannequin.getEquipment().setHelmetDropChance(0f);

        return mannequin;
    }
}
