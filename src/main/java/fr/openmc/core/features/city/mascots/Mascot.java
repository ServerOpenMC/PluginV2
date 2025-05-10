package fr.openmc.core.features.city.mascots;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;

import java.util.UUID;

@Setter
@Getter
public class Mascot {

    private String cityUuid;
    private UUID mascotUuid;
    private int level;
    private boolean immunity;
    private boolean alive;
    private Chunk chunk;

    public Mascot(String cityUuid, UUID mascotUuid, int level, boolean immunity, boolean alive, Chunk chunk) {
        this.cityUuid = cityUuid;
        this.mascotUuid = mascotUuid;
        this.level = level;
        this.immunity = immunity;
        this.alive = alive;
        this.chunk = chunk;
    }
}

