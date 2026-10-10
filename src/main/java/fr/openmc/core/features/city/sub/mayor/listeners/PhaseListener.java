package fr.openmc.core.features.city.sub.mayor.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.models.MayorPhase;
import fr.openmc.core.utils.text.DateUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class PhaseListener {

    /**
     * Constructor for the PhaseListener class.
     * This class is responsible for managing the phases of the mayor's election process.
     *
     * @param plugin The OMCPlugin instance.
     */
    public PhaseListener(MayorManager mayorManager, OMCPlugin plugin) {
        BukkitRunnable eventRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                MayorPhase phase = mayorManager.getMayorPhase();

                // PHASE 1 - Elections - Mardi à Mercredi
                if (phase.equals(MayorPhase.MAYOR_ELECTED) && MayorPhase.OPEN_ELECTION.getStartDay() == DateUtils.getCurrentDayOfWeek()) {
                    phase.getRunnable().run();
                }

                // PHASE 2 - Maire Elu - Jeudi à Jeudi Prochain
                if (phase.equals(MayorPhase.OPEN_ELECTION) && MayorPhase.MAYOR_ELECTED.getStartDay() == DateUtils.getCurrentDayOfWeek()) {
                    phase.getRunnable().run();
                }
            }
        };
        // 1200 s = 1 min
        eventRunnable.runTaskTimer(plugin, 0, 1200);
    }

}
