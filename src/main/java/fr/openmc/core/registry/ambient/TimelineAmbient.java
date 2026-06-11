package fr.openmc.core.registry.ambient;

import fr.openmc.api.datapacks.injectors.TimelineInjector;

public interface TimelineAmbient {
    TimelineInjector.TimelineBuilder getTimelineBuilder();

    default TimelineInjector toTimelineInjector(String namespace, String id) {
        return new TimelineInjector(namespace).add(id, getTimelineBuilder());
    }
}
