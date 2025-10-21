package fr.openmc.core.features.city.sub.mayor.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.utils.DateUtils;
import org.bukkit.scheduler.BukkitRunnable;

import static fr.openmc.core.features.city.sub.mayor.managers.MayorManager.PHASE_1_DAY;
import static fr.openmc.core.features.city.sub.mayor.managers.MayorManager.PHASE_2_DAY;

public class PhaseListener {

    /**
     * Constructor for the PhaseListener class.
     * This class is responsible for managing the phases of the mayor's election process.
     *
     * @param plugin The OMCPlugin instance.
     */
    public PhaseListener(OMCPlugin plugin) {
        BukkitRunnable eventRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                int phase = MayorManager.phaseMayor;

                // PHASE 1 - Elections - Mardi à Mercredi
                if (phase == 2 && PHASE_1_DAY == DateUtils.getCurrentDayOfWeek()) {
                    MayorManager.initPhase1();
                }

                // PHASE 2 - Maire Elu - Jeudi à Jeudi Prochain
                if (phase == 1 && PHASE_2_DAY == DateUtils.getCurrentDayOfWeek()) {
                    MayorManager.initPhase2();
                }
            }
        };
        // 1200 s = 1 min
        eventRunnable.runTaskTimer(plugin, 0, 1200);
    }

}
