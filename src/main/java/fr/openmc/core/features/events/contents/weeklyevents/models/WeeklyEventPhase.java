package fr.openmc.core.features.events.contents.weeklyevents.models;

import net.kyori.adventure.text.Component;

import java.time.DayOfWeek;
import java.util.List;

public abstract class WeeklyEventPhase {
    public abstract Component getName();
    public abstract List<Component> getDescription();
    public abstract DayOfWeek getStartDay();
    public abstract int getStartHour();
    public abstract int getStartMinutes();
    public abstract Runnable runAction();
}
