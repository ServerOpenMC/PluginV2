package fr.openmc.core.features.events.contents.dailyevents.models.dailyevent;

import fr.openmc.core.registry.ambient.CustomAmbient;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Wrapper sous forme d'interface afin d'implementer les ambiences dans les événnements.
 */
public interface HasAmbient {
    CustomAmbient getAmbient();

    default void apply(Player player) {
        apply(player, false);
    }

    default void apply(Player player, boolean isJoining) {
        this.getAmbient().apply(player, isJoining);
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
