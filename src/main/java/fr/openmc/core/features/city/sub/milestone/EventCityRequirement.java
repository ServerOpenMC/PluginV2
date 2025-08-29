package fr.openmc.core.features.city.sub.milestone;

import org.bukkit.event.Event;

/**
 * Représente une exigence de ville basée sur des évènements.
 * Cette interface étend CityRequirement et permet de gérer des évènements spécifiques liés à une exigence.
 */
public interface EventCityRequirement extends CityRequirement {

    /**
     * Traite l'évènement lié à cette exigence.
     *
     * @param event l'évènement à traiter
     */
    void onEvent(Event event);
}
