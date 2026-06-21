package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.registry;

import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.mobs.CustomMobEntry;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;

@Getter
public class SeaCreatureLoot implements CustomLoot {
    private final double chance;
    private final CustomMobEntry seaCreatureMob;

    public SeaCreatureLoot(CustomMobEntry seaCreatureMob, double chance) {
        this.chance = chance;
        this.seaCreatureMob = seaCreatureMob;
    }

    @Override
    public Component getDisplayText() {
        return Component.text(seaCreatureMob.getMob().getName());
    }

    @Override
    public Set<CustomLoot> run(Player receiver) {
        // déjà lancé dans MiraculousFishingManager.simulateLaunchLoot
        return Collections.singleton(this);
    }
}