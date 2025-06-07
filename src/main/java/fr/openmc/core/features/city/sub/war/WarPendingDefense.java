package fr.openmc.core.features.city.sub.war;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class WarPendingDefense {

    @Getter
    private final City defender;
    @Getter
    private final City attacker;
    @Getter
    private List<UUID> attackers = new ArrayList<>();
    @Getter
    private final Set<UUID> acceptedDefenders = new HashSet<>();
    @Getter
    private final int required;
    @Getter
    @Setter
    private boolean alreadyExecuted = false;

    public WarPendingDefense(City attacker, City defender, List<UUID> attackers, int required) {
        this.defender = defender;
        this.attacker = attacker;
        this.attackers = attackers;
        this.required = required;
    }

    public boolean accept(UUID uuid) {
        if (acceptedDefenders.size() >= required) return false;
        return acceptedDefenders.add(uuid);
    }

}
