package fr.openmc.core.features.city.events;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CityCreationEvent extends Event {

    @Getter private final City city;
    @Getter private final Player owner;

    private static final HandlerList HANDLERS = new HandlerList();

    public CityCreationEvent(City city, Player owner) {
        this.city = city;
        this.owner = owner;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
