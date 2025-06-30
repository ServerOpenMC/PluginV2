package fr.openmc.core.features.milestones;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.milestones.tutorial.TutorialMilestone;
import fr.openmc.core.features.quests.objects.Quest;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class MilestonesManager {

    private final Set<Milestone> milestones;

    public MilestonesManager() {
        this.milestones = new HashSet<>();

        registerMilestones(
                new TutorialMilestone()
        );
    }

    /**
     * Enregistre tous les milestones
     */
    public void registerMilestones(Milestone... milestones) {
        for (Milestone milestone : milestones) {
            if (milestone != null) {
                this.milestones.add(milestone);
                registerQuestMilestone(milestone);
            }
        }
    }

    public void registerMilestoneCommand() {
        CommandsManager.getHandler().register(new MilestoneCommand(this.milestones));
    }

    public void registerQuestMilestone(Milestone milestone) {
        for (Quest quest : milestone.getSteps()) {
            if (quest instanceof Listener listener) {
                Bukkit.getPluginManager().registerEvents(listener, OMCPlugin.getInstance());
            }
        }
    }
}
