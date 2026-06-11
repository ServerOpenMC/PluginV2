package fr.openmc.core.registry.ambient;

import fr.openmc.api.datapacks.injectors.TimelinesInjector;

public interface TimelineAmbient {
    TimelinesInjector.TimelineBuilder getTimelineBuilder();

    default TimelinesInjector toTimelineInjector(String namespace, String id) {
        return new TimelinesInjector(namespace).add(id, getTimelineBuilder());
    }
}
