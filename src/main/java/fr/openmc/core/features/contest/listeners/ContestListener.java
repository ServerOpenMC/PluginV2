package fr.openmc.core.features.contest.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.utils.DateUtils;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ContestListener implements Listener {
    public ContestListener(OMCPlugin plugin) {
        //attention ne pas modifier les valeurs de départ des contest sinon le systeme va broke
        BukkitRunnable eventRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E", Locale.FRENCH);
                DayOfWeek dayStartContestOfWeek = DayOfWeek.from(formatter.parse(ContestManager.data.getStartdate()));
                int phase = ContestManager.data.getPhase();

                if (phase == 1 && DateUtils.getCurrentDayOfWeek().getValue() == dayStartContestOfWeek.getValue()) {
                    ContestManager.initPhase1();
                }
                int dayStart = dayStartContestOfWeek.getValue() + 1;
                if (dayStart == 8) {
                    dayStart = 1;
                }
                if (phase == 2 && DateUtils.getCurrentDayOfWeek().getValue() == dayStart) {
                    ContestManager.initPhase2();
                }
                int dayEnd = dayStart + 2;
                if (dayEnd >= 8) {
                    dayEnd = 1;
                } //attention ne pas modifier les valeurs de départ des contest sinon le systeme va broke
                if (phase == 3 && DateUtils.getCurrentDayOfWeek().getValue() == dayEnd) {
                    ContestManager.initPhase3();
                }
            }
        };
        // 1200 s = 1 min
        eventRunnable.runTaskTimer(plugin, 0, 1200);
     }
}
