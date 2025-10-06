package fr.openmc.api.cooldown;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class CooldownEndEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID cooldownUUID;
    private final String group;

    public CooldownEndEvent(UUID cooldownUUID, String group) {
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
