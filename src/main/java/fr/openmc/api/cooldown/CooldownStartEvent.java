package fr.openmc.api.cooldown;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CooldownStartEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    @Getter private final UUID cooldownUUID;
    @Getter private final String group;

    public CooldownStartEvent(UUID cooldownUUID, String group) {
        this.cooldownUUID = cooldownUUID;
        this.group = group;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
