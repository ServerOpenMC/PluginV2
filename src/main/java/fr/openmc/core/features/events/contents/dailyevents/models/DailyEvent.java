package fr.openmc.core.features.events.contents.dailyevents.models;

import fr.openmc.core.features.events.models.Event;

public abstract class DailyEvent extends Event {
    public abstract String getEventId();

    public abstract int getDuration();
    public abstract Runnable onStart();
    public abstract Runnable onEnd();
}
