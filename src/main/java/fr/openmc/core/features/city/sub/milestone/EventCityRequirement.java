package fr.openmc.core.features.city.sub.milestone;

import org.bukkit.event.Event;

public interface EventCityRequirement extends CityRequirement {
    void onEvent(Event event);
}
