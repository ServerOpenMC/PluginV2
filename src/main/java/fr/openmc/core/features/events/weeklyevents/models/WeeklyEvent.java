package fr.openmc.core.features.events.weeklyevents.models;

import fr.openmc.core.features.events.weeklyevents.WeeklyEventsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class WeeklyEvent {
    public abstract Component getName();
    public abstract List<Component> getDescription();
    public abstract ItemStack getIcon();
    public abstract List<WeeklyEventPhase> getPhases();

    /**
     * Retourne true si on est temporellement dans une phase active de cet event.
     */
    public boolean isActive() {
        return WeeklyEventsManager.getCurrentEvent() == this
                && WeeklyEventsManager.isEventActive();
    }

    /**
     * Retourne la phase active de cet event, ou null si l'event n'est pas actif.
     */
    public WeeklyEventPhase getActivePhase() {
        if (!isActive()) return null;
        return WeeklyEventsManager.getCurrentPhase();
    }
}
