package fr.openmc.core.registry.ambient;

import fr.openmc.api.datapacks.builders.TimelineBuilder;
import fr.openmc.api.datapacks.injectors.TimelinesInjector;

public interface TimelineAmbient {
    TimelineBuilder getTimelineBuilder();

    default TimelinesInjector toTimelineInjector(String namespace, String id) {
        return new TimelinesInjector(namespace).add(id, getTimelineBuilder());
    }
}
