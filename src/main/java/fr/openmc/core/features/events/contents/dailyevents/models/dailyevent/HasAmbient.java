package fr.openmc.core.features.events.contents.dailyevents.models.dailyevent;

import fr.openmc.core.registry.ambient.CustomAmbient;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface HasAmbient {
    CustomAmbient getAmbient();

    default void apply(Player player) {
        this.getAmbient().apply(player);
    }

    default void apply(Collection<Player> receivers) {
        this.getAmbient().apply(receivers);
    }

    default void reset(Player player) {
        this.getAmbient().reset(player);
    }

    default void reset(Collection<Player> receivers) {
        this.getAmbient().reset(receivers);
    }
}
