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

    /**
     * Accepts a defender's participation in the war.
     * If the number of accepted defenders reaches the required amount, it returns false.
     *
     * @param uuid The UUID of the defender.
     * @return true if the defender was accepted, false if the required number of defenders has already been reached.
     */
    public boolean accept(UUID uuid) {
        if (acceptedDefenders.size() >= required) return false;
        return acceptedDefenders.add(uuid);
    }

}
