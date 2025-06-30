package fr.openmc.core.features.millestones;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.millestones.tutorial.TutorialMillestone;

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
     *
     * @return Instance of MillestonesManager
     */
    public MillestonesManager registerMillestones(Millestone... millestones) {
        for (Millestone millestone : millestones) {
            if (millestone != null) {
                this.millestones.add(millestone);
            }
        }
        return this;
    }

    public MillestonesManager registerMillestoneCommand() {
        CommandsManager.getHandler().register(new MillestoneCommand(this.millestones));
        return this;
    }
}
