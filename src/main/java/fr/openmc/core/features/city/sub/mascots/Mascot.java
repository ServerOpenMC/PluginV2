package fr.openmc.core.features.city.sub.mascots;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;

import java.util.UUID;

@Setter
@Getter
public class Mascot {

    private City city;
    private UUID mascotUUID;
    private int level;
    private boolean immunity;
    private boolean alive;
    private Chunk chunk;

    public Mascot(City city, UUID mascotUUID, int level, boolean immunity, boolean alive, Chunk chunk) {
        this.city = city;
        this.mascotUUID = mascotUUID;
        this.level = level;
        this.immunity = immunity;
        this.alive = alive;
        this.chunk = chunk;
    }
}

