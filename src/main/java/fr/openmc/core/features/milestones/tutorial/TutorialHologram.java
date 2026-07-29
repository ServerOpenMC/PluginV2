package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.core.features.displays.holograms.Hologram;
import fr.openmc.core.utils.text.fonts.CustomFonts;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TutorialHologram extends Hologram {

    public TutorialHologram() {
        super("tutorial");

        String icon = CustomFonts.getBest("omc_icons:openmc", "");
        this.setLines(
                TranslationManager.translation(
                        "feature.milestones.tutorial.hologram.icon",
                        Component.text(icon).color(NamedTextColor.WHITE)
                ),
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
