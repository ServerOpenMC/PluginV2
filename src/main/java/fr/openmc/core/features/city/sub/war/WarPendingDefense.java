package fr.openmc.core.features.city.sub.war;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class WarPendingDefense {

    private final City defender;
    private final City attacker;
    private final List<UUID> attackers;
    private final Set<UUID> acceptedDefenders = new HashSet<>();
    private final int required;
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
