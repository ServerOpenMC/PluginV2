package fr.openmc.core.features.city.sub.milestone.events;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class CityUpgradeEvent extends Event {

    private final City city;

    private static final HandlerList HANDLERS = new HandlerList();

    public CityUpgradeEvent(City city) {
        this.city = city;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
