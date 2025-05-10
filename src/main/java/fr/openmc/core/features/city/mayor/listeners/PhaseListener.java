package fr.openmc.core.features.city.mayor.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.utils.DateUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

import static fr.openmc.core.features.city.mayor.managers.MayorManager.PHASE_1_DAY;
import static fr.openmc.core.features.city.mayor.managers.MayorManager.PHASE_2_DAY;

public class PhaseListener {

    /**
     * Constructor for the PhaseListener class.
     * This class is responsible for managing the phases of the mayor election process.
     *
     * @param plugin The OMCPlugin instance.
     */
    public PhaseListener(OMCPlugin plugin) {
        MayorManager mayorManager = MayorManager.getInstance();
        BukkitRunnable eventRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                int phase = mayorManager.phaseMayor;

                // PHASE 1 - Elections - Mardi à Mercredi
                if (phase == 2 && PHASE_1_DAY == DateUtils.getCurrentDayOfWeek()) {
                    try {
                        mayorManager.initPhase1();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                // PHASE 2 - Maire Elu - Jeudi à Jeudi Prochain
                if (phase == 1 && PHASE_2_DAY == DateUtils.getCurrentDayOfWeek()) {
                    mayorManager.initPhase2();
                }
            }
        };
        // 1200 s = 1 min
        eventRunnable.runTaskTimer(plugin, 0, 1200);
    }

}
