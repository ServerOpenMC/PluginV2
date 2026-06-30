package fr.openmc.core.features.events.contents.dailyevents.models.dailyevent;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Interface implémentant la gestion de Broadcast
 */
public interface HasBroadcast {
    Component getStartBroadcast();
    Component getEndBroadcast();

    default void sendStartBroadcast(Collection<Player> receivers) {
        for (Player receiver : receivers) {
            receiver.sendMessage(getStartBroadcast());
        }
    }

    default void sendEndBroadcast(Collection<Player> receivers) {
        for (Player receiver : receivers) {
            receiver.sendMessage(getEndBroadcast());
        }
    }
}
