package fr.openmc.core.features.city.events;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class CityMoneyUpdateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final double difference;
    private final double before;
    private final double after;
    private final City city;

    public CityMoneyUpdateEvent(City city, double before, double after) {
        this.city = city;
        this.before = before;
        this.after = after;
        this.difference = after - before;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
