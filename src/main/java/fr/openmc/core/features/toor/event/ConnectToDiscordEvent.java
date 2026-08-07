package fr.openmc.core.features.toor.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class ConnectToDiscordEvent extends Event {

    private final UUID playerUUID;
    private final String discordId;
    private final String discordUsername;

    private static final HandlerList HANDLERS = new HandlerList();

    public ConnectToDiscordEvent(UUID playerUUID, String discordId, String discordUsername) {
        this.playerUUID = playerUUID;
        this.discordId = discordId;
        this.discordUsername = discordUsername;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
