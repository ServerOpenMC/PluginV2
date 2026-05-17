package fr.openmc.core.hooks.itemsadder.events;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IAItemLoadEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final String namespace;
    @Getter
    private final String itemId;
    @Getter
    private final ConfigurationSection itemConfig;

	public IAItemLoadEvent(String namespace, String itemId, ConfigurationSection itemConfig) {
        this.itemConfig = itemConfig;
        this.itemId = itemId;
        this.namespace = namespace;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
