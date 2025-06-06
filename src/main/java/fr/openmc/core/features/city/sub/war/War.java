package fr.openmc.core.features.city.sub.war;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

import static fr.openmc.core.features.city.sub.war.WarManager.TIME_FIGHT;
import static fr.openmc.core.features.city.sub.war.WarManager.TIME_PREPARATION;

public class War {

    public enum WarPhase {PREPARATION, COMBAT, ENDED}

    @Getter
    private final City cityAttacker;
    @Getter
    private final City cityDefender;
    @Getter
    private final List<UUID> attackers;
    @Getter
    private final List<UUID> defenders;
    @Getter
    private WarPhase phase = WarPhase.PREPARATION;
    @Getter
    private long startTime;

    public War(City cityAttacker, City cityDefender, List<UUID> attackers, List<UUID> defenders) {
        this.cityAttacker = cityAttacker;
        this.cityDefender = cityDefender;
        this.attackers = attackers;
        this.defenders = defenders;

        startPreparation();
    }

    public void startPreparation() {
        this.startTime = System.currentTimeMillis();
        this.phase = WarPhase.PREPARATION;

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), this::startCombat, (long) TIME_PREPARATION * 60 * 20);
    }

    public int getPreparationTimeRemaining() {
        if (phase != WarPhase.PREPARATION) return 0;
        long elapsed = (System.currentTimeMillis() - startTime) / 1000L;
        return Math.max(0, TIME_PREPARATION * 60 - (int) elapsed);
    }

    public void startCombat() {
        this.phase = WarPhase.COMBAT;

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), this::end, (long) TIME_FIGHT * 60 * 20);
    }

    public int getCombatTimeRemaining() {
        if (phase != WarPhase.COMBAT) return 0;
        long elapsed = (System.currentTimeMillis() - startTime) / 1000L;
        return Math.max(0, TIME_FIGHT * 60 - (int) elapsed);
    }

    public void end() {
        this.phase = WarPhase.ENDED;

        WarManager.endWar(this);
    }
}
