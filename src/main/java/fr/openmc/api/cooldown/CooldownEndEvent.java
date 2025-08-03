package fr.openmc.api.cooldown;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CooldownEndEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final String uuid;
    @Getter
    private final String group;

    public CooldownEndEvent(String uuid, String group) {
        this.uuid = uuid;
        this.group = group;
    }

    public String getUUID() {
        return uuid;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
