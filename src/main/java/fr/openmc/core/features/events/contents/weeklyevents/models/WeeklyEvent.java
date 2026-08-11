package fr.openmc.core.features.events.contents.weeklyevents.models;

import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.models.Event;

import java.util.List;
import java.util.Optional;

public abstract class WeeklyEvent extends Event {
    public abstract String getId();
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
     * Retourne la premiere phase de l'event
     * @return un WeeklyEventPhase
     */
    public WeeklyEventPhase getFirstPhase() {
        return getPhases().getFirst();
    }

    public Optional<WeeklyEventPhase> getNextPhase(WeeklyEventPhase current) {
        List<WeeklyEventPhase> phases = getPhases();
        int index = phases.indexOf(current);
        if (index == -1 || index + 1 >= phases.size()) return Optional.empty();

        return Optional.of(phases.get(index + 1));
    }

    public boolean hasPhase(WeeklyEventPhase phase) {
        return getPhases().contains(phase);
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
