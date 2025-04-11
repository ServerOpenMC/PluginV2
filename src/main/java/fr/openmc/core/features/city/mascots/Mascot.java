package fr.openmc.core.features.city.mascots;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Mascot {

    private String cityUuid;
    private String mascotUuid;
    private int level;
    private boolean immunity;
    private boolean alive;

    public Mascot(String cityUuid, String mascotUuid, int level, boolean immunity, boolean alive) {
        this.cityUuid = cityUuid;
        this.mascotUuid = mascotUuid;
        this.level = level;
        this.immunity = immunity;
        this.alive = alive;
    }
}

