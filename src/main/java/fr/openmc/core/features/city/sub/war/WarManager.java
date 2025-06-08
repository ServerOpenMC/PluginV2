package fr.openmc.core.features.city.sub.war;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.war.commands.AdminWarCommand;
import fr.openmc.core.features.city.sub.war.commands.WarCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WarManager {

    public static int TIME_PREPARATION = 10; // in minutes
    public static int TIME_FIGHT = 30; // in minutes

    public static long CITY_IMMUNITY_FIGHT_COOLDOWN = 2 * 24 * 60 * 60 * 1000L; // 2 jours en millisecondes

    public static final Map<String, War> warsByAttacker = new HashMap<>();
    public static final Map<String, War> warsByDefender = new HashMap<>();

    private static final Map<String, WarPendingDefense> pendingDefenses = new HashMap<>();

    public WarManager() {
        CommandsManager.getHandler().register(
                new WarCommand(),
                new AdminWarCommand()
        );
    }

    public static boolean isCityInWar(String cityUUID) {
        return warsByAttacker.containsKey(cityUUID) || warsByDefender.containsKey(cityUUID);
    }

    public static War getWarByCity(String cityUUID) {
        War war = warsByAttacker.get(cityUUID);
        if (war != null) return war;

        war = warsByDefender.get(cityUUID);
        if (war != null) return war;

        return null;
    }

    public static void startWar(City attacker, City defender, List<UUID> attackers, List<UUID> defenders) {
        War war = new War(attacker, defender, attackers, defenders);

        warsByAttacker.put(attacker.getUUID(), war);
        warsByDefender.put(defender.getUUID(), war);
    }

    public static void endWar(War war) {
        warsByAttacker.remove(war.getCityAttacker().getUUID());
        warsByDefender.remove(war.getCityDefender().getUUID());
    }

    public static String getFormattedPhase(War.WarPhase phase) {
        return switch (phase) {
            case PREPARATION -> "Préparation";
            case COMBAT -> "Combat";
            case ENDED -> "Fin";
        };
    }

    public static void addPendingDefense(WarPendingDefense defense) {
        pendingDefenses.put(defense.getDefender().getUUID(), defense);
    }

    public static WarPendingDefense getPendingDefenseFor(City city) {
        return pendingDefenses.get(city.getUUID());
    }
}
