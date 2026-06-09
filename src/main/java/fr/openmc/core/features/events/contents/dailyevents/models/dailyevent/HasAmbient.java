package fr.openmc.core.features.events.contents.dailyevents.models.dailyevent;

import fr.openmc.core.registry.ambient.CustomAmbient;
import org.bukkit.entity.Player;

public interface HasAmbient {
    CustomAmbient getAmbient();

    default void apply(Player player) {
        this.getAmbient().apply(player);
    }

    default void reset(Player player) {
        this.getAmbient().reset(player);
    }
}
