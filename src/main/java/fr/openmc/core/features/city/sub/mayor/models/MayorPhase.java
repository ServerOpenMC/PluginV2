package fr.openmc.core.features.city.sub.mayor.models;

import fr.openmc.core.OMCRegistry;
import lombok.Getter;

import java.time.DayOfWeek;

@Getter
public enum MayorPhase {
    OPEN_ELECTION(DayOfWeek.TUESDAY, () ->
            OMCRegistry.CITY_FEATURES.MAYOR.initOpenElectionPhase()),
    MAYOR_ELECTED(DayOfWeek.THURSDAY, () ->
            OMCRegistry.CITY_FEATURES.MAYOR.initElectedMayorPhase())
    ;

    private final DayOfWeek startDay;
    private final Runnable runnable;

    MayorPhase(DayOfWeek startDay, Runnable runnable) {
        this.startDay = startDay;
        this.runnable = runnable;
    }
}
