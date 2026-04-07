package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.core.features.displays.holograms.Hologram;
import fr.openmc.core.utils.text.fonts.CustomFonts;

public class TutorialHologram extends Hologram {

    public TutorialHologram() {
        super("tutorial");

        this.setLines(
                "§f" + CustomFonts.getBest("omc_icons:openmc", "§f"),
                "§fBienvenue sur §dOpenMC V2§f!",
                "§fCette version est basée sur les §2villes",
                "§f",
                "§fPour accéder au tutoriel, utilisez la commande §a/milestones§f.",
                "§fC'est votre §dserveur §f!",
                "§8§m                                  §r",
                "§fLiens utiles : §5/socials"

        );
        this.setScale(0.5f);
        this.setLocation(0, 2, 0);
    }
}
