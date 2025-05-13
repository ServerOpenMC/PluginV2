package fr.openmc.core.features.corporation.company;

import fr.openmc.core.features.city.City;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CompanyOwner {

    private final City city;
    private final UUID player;

    public CompanyOwner(City city) {
        this.city = city;
        this.player = null;
    }

    public CompanyOwner(UUID owner) {
        this.city = null;
        this.player = owner;
    }

    /**
     * know if the owner is a city
     *
     * @return true if it's a city
     */
    public boolean isCity() {
        return city != null;
    }

    /**
     * know if teh owner is a player
     *
     * @return true if it's a player
     */
    public boolean isPlayer() {
        return player != null;
    }

}
