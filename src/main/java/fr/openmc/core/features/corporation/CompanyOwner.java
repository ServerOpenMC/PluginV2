package fr.openmc.core.features.corporation;

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

    public boolean isCity() {
        return city != null;
    }

    public boolean isPlayer() {
        return player != null;
    }

}
