package fr.openmc.core.features.events.contents.dailyevents.models;

import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SlotDailyEvent {
    MONDAY(LocalTime.of(18, 30), LocalTime.of(21, 0)),
    TUESDAY(LocalTime.of(18, 30), LocalTime.of(21, 0)),
    WEDNESDAY(LocalTime.of(15, 0), LocalTime.of(18, 30), LocalTime.of(21, 0)),
    THURSDAY(LocalTime.of(18, 30), LocalTime.of(21, 0)),
    FRIDAY(LocalTime.of(17, 0), LocalTime.of(18, 30), LocalTime.of(21, 0)),
    SATURDAY(LocalTime.of(10, 0), LocalTime.of(15, 0), LocalTime.of(21, 0)),
    SUNDAY(LocalTime.of(10, 0), LocalTime.of(15, 0), LocalTime.of(21, 0));

    @Getter
    private final List<LocalTime> slots;

    SlotDailyEvent(LocalTime... slots) {
        this.slots = Arrays.stream(slots)
                .sorted()
                .toList();
    }

    public static SlotDailyEvent of(DayOfWeek day) {
        return valueOf(day.name());
    }

    public static LocalDateTime getNextSlot(LocalDateTime now) {
        for (int i = 0; i <= 7; i++) {
            LocalDate date = now.toLocalDate().plusDays(i);
            for (LocalTime slot : of(date.getDayOfWeek()).getSlots()) {
                LocalDateTime testDate = date.atTime(slot);
                if (testDate.isAfter(now))
                    return testDate;
            }
        }
        return null;
    }

    public static List<LocalDateTime> getSlotsBetween(LocalDateTime from, LocalDateTime to) {
        List<LocalDateTime> result = new ArrayList<>();
        LocalDate date = from.toLocalDate();
        while (!date.isAfter(to.toLocalDate())) {
            for (LocalTime slot : of(date.getDayOfWeek()).getSlots()) {
                LocalDateTime candidate = date.atTime(slot);
                if (candidate.isAfter(from) && !candidate.isAfter(to)) {
                    result.add(candidate);
                }
            }
            date = date.plusDays(1);
        }
        return result;
    }
}
