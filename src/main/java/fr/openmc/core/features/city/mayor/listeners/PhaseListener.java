package fr.openmc.core.features.city.mayor.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.utils.DateUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;

public class PhaseListener {
    public PhaseListener(OMCPlugin plugin) {
        MayorManager mayorManager = MayorManager.getInstance();
        BukkitRunnable eventRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                int phase = mayorManager.phaseMayor;

                // PHASE 1 - Elections - Mardi à Mercredi
                if (phase == 0 && DayOfWeek.TUESDAY == DateUtils.getCurrentDayOfWeek()) {
                    mayorManager.initPhase1();
                }

                // PHASE 2 - Maire Elu - Jeudi à Jeudi Prochain
                if (phase == 1 && DayOfWeek.THURSDAY == DateUtils.getCurrentDayOfWeek()) {
                    mayorManager.initPhase2();
                }
            }
        };
        // 1200 s = 1 min
        eventRunnable.runTaskTimer(plugin, 0, 1200);
    }

}
