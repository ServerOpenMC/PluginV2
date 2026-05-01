package fr.openmc.core.features.events.contents.weeklyevents.models;

import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.models.Event;

import java.util.List;

public abstract class WeeklyEvent extends Event {
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

    /**
     * Retourne le nombre de semaines d'offset entre la semaine actuelle et la semaine de référence pour cet event.
     * Utile pour prédire les événements lointin
     * @return Offset
     */
    public int getWeekOffset() {
        return 0;
    }
}
