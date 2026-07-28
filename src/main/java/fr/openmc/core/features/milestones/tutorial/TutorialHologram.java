package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.core.features.displays.holograms.Hologram;
import fr.openmc.core.utils.text.messages.TranslationManager;

public class TutorialHologram extends Hologram {

    public TutorialHologram() {
        super("tutorial");

        this.setLines(
                TranslationManager.translation("feature.milestones.tutorial.hologram.icon"),
                TranslationManager.translation("feature.milestones.tutorial.hologram.welcome"),
                TranslationManager.translation("feature.milestones.tutorial.hologram.based_on"),
                TranslationManager.translation("empty"),
                TranslationManager.translation("feature.milestones.tutorial.hologram.how_to"),
                TranslationManager.translation("feature.milestones.tutorial.hologram.server"),
                TranslationManager.translation("feature.milestones.tutorial.hologram.separator"),
                TranslationManager.translation("feature.milestones.tutorial.hologram.links")

        );
        this.setScale(0.5f);
        this.setLocation(0, 2, 0);
    }
}
