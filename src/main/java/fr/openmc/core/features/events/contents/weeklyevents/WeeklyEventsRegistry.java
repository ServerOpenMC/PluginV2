package fr.openmc.core.features.events.contents.weeklyevents;

import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.Contest;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEvent;

import java.util.List;
import java.util.Optional;

public class WeeklyEventsRegistry extends Registry<String, WeeklyEvent>
        implements KeyedRegistry<String, WeeklyEvent>  {

    public final WeeklyEvent CONTEST = register(new Contest());

    @Override
    public String key(WeeklyEvent registryObject) {
        return registryObject.getId();
    }

    public WeeklyEvent getEvent(Class<? extends WeeklyEvent> eventClass) {
        return entries.values().stream()
                .filter(weeklyEvent -> weeklyEvent.getClass().equals(eventClass))
                .findFirst()
                .orElse(null);
    }

    public Optional<WeeklyEvent> getNextEvent(WeeklyEvent current) {
        List<WeeklyEvent> events = values().stream().toList();
        int index = events.indexOf(current);
        if (index == -1) return Optional.empty();
        return Optional.of(events.get((index + 1) % events.size()));
    }
}
