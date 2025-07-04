package fr.openmc.core.features.milestones.tutorial;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.features.displays.holograms.Hologram;

public class TutorialHologram extends Hologram {

    public TutorialHologram() {
        super("tutorial");

        this.setLines(
                "§f" + new FontImageWrapper("omc_icons:openmc").getString(),
                "§fBienvenue sur §dOpenMC V2§f!",
                "§fCette version est basée sur les §2Villes",
                "§f",
                "§fPour acceder au tutoriel, utilisez la commande §a/milestone tutorial§f.",
                "§fC'est votre §dserveur §f!",
                "§8§m                                  §r",
                "§fLiens utiles : §5/socials"

        );
        this.setScale(0.5f);
        this.setLocation(0, 2, 0);
    }
}
