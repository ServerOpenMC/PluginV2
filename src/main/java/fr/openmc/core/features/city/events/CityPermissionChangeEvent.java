package fr.openmc.core.features.city.events;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class CityPermissionChangeEvent extends Event {

    private final City city;
    private final OfflinePlayer member;
    private final CityPermission permission;
    private final boolean value;

    private static final HandlerList HANDLERS = new HandlerList();

    public CityPermissionChangeEvent(City city, OfflinePlayer member, CityPermission permission, boolean value) {
        this.city = city;
        this.member = member;
        this.permission = permission;
        this.value = value;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
