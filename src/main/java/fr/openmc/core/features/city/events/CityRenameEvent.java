package fr.openmc.core.features.city.events;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CityRenameEvent extends Event {

    @Getter private String oldName;
    @Getter private City city;

    public CityRenameEvent(String oldName, City city) {
        this.oldName = oldName;
        this.city = city;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
