package fr.openmc.core.features.contest.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.contest.managers.ContestManager;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ContestListener implements Listener {
    private final ContestManager contestManager;
    public ContestListener(OMCPlugin plugin) {
        contestManager = ContestManager.getInstance();
        //attention ne pas modifier les valeurs de départ des contest sinon le systeme va broke
        BukkitRunnable eventRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E", Locale.FRENCH);
                DayOfWeek dayStartContestOfWeek = DayOfWeek.from(formatter.parse(contestManager.data.getStartdate()));
                int phase = contestManager.data.getPhase();

                if (phase == 1 && contestManager.getCurrentDayOfWeek().getValue() == dayStartContestOfWeek.getValue()) {
                    contestManager.initPhase1();
                }
                int dayStart = dayStartContestOfWeek.getValue() + 1;
                if (dayStart == 8) {
                    dayStart = 1;
                }
                if (phase == 2 && contestManager.getCurrentDayOfWeek().getValue() == dayStart) {
                    contestManager.initPhase2();
                }
                int dayEnd = dayStart + 2;
                if (dayEnd >= 8) {
                    dayEnd = 1;
                } //attention ne pas modifier les valeurs de départ des contest sinon le systeme va broke
                if (phase == 3 && contestManager.getCurrentDayOfWeek().getValue() == dayEnd) {
                    contestManager.initPhase3();
                }
            }
        };
        // 1200 s = 1 min
        eventRunnable.runTaskTimer(plugin, 0, 1200);
     }
}
