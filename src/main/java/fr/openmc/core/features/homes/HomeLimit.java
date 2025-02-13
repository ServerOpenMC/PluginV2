package fr.openmc.core.features.homes;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class HomeLimit {

    private final UUID playerUUID;
    @Setter private HomeLimits homeLimit;

    public HomeLimit(UUID playerUUID, HomeLimits limit) {
        this.playerUUID = playerUUID;
        this.homeLimit = limit;
    }
}