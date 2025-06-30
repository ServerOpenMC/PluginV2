package fr.openmc.core.features.millestones;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.millestones.tutorial.TutorialMillestone;
import fr.openmc.core.features.quests.objects.Quest;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class MillestonesManager {

    private final Set<Millestone> millestones;

    public MillestonesManager() {
        this.millestones = new HashSet<>();

        registerMillestones(
                new TutorialMillestone()
        );
    }

    /**
     * Enregistre tous les millestones
     */
    public void registerMillestones(Millestone... millestones) {
        for (Millestone millestone : millestones) {
            if (millestone != null) {
                this.millestones.add(millestone);
                registerQuestMillestone(millestone);
            }
        }
    }

    public void registerMillestoneCommand() {
        CommandsManager.getHandler().register(new MillestoneCommand(this.millestones));
    }

    public void registerQuestMillestone(Millestone millestone) {
        for (Quest quest : millestone.getSteps()) {
            if (quest instanceof Listener listener) {
                Bukkit.getPluginManager().registerEvents(listener, OMCPlugin.getInstance());
            }
        }
    }
}
