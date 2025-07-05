package fr.openmc.core.features.economy.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class BankDepositEvent extends Event {

    private final UUID UUID;
    private static final HandlerList HANDLERS = new HandlerList();

    public BankDepositEvent(UUID uuid) {
        this.UUID = uuid;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
