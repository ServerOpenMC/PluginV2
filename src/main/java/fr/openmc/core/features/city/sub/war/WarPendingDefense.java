package fr.openmc.core.features.city.sub.war;

import fr.openmc.core.features.city.City;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WarPendingDefense {

    @Getter
    private final City defender;
    @Getter
    private final Set<UUID> acceptedDefenders = new HashSet<>();
    private final int required;

    public WarPendingDefense(City defender, int required) {
        this.defender = defender;
        this.required = required;
    }

    public boolean accept(UUID uuid) {
        if (acceptedDefenders.size() >= required) return false;
        return acceptedDefenders.add(uuid);
    }

}
