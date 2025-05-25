package fr.openmc.core.features.city.mascots;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;

import java.util.UUID;

@Setter
@Getter
public class Mascot {

    private String cityUUID;
    private UUID mascotUUID;
    private int level;
    private boolean immunity;
    private boolean alive;
    private Chunk chunk;

    public Mascot(String cityUUID, UUID mascotUUID, int level, boolean immunity, boolean alive, Chunk chunk) {
        this.cityUUID = cityUUID;
        this.mascotUUID = mascotUUID;
        this.level = level;
        this.immunity = immunity;
        this.alive = alive;
        this.chunk = chunk;
    }
}

