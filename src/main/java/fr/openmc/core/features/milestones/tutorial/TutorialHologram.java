package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.core.features.displays.holograms.Hologram;

public class TutorialHologram extends Hologram {

    public TutorialHologram() {
        super("tutorial");

        this.setLines(
                ":openmc:",
                "§aBienvenue sur OpenMC !",
                "§bPour commencer,",
                "§ccliquez sur le panneau",
                "§dà votre droite !"
        );
        this.setScale(0.5f);
        this.setLocation(0, 2, 0);
    }
}
