package fr.openmc.core.features.singularity.sub.worldsfx;

import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.features.singularity.sub.worldsfx.sfx.ImpulsionSingularitySFX;
import fr.openmc.core.features.singularity.sub.worldsfx.sfx.InstabilitySingularitySFX;
import fr.openmc.core.features.singularity.sub.worldsfx.sfx.PulseSingularitySFX;
import fr.openmc.core.registry.worldtemplates.WorldTemplate;
import lombok.Getter;
import org.bukkit.Location;

/**
 * Classe gérant les SFX (Effets Spéciaux) de la Dimension inclus dedans :
 * - les Impulsions de la Singularité
 * - la gravité des joueurs
 * - les intéractions avec la Singularité
 */
public class SingularityWorldManager extends Feature {

    public static Location origin;
    public static WorldTemplate worldTemplate;

    @Getter
    private PulseSingularitySFX pulseSingularitySFX;
    @Getter
    private ImpulsionSingularitySFX impulsionSingularitySFX;
    @Getter
    private InstabilitySingularitySFX instabilitySingularitySFX;

    public SingularityWorldManager(WorldTemplate template) {
        worldTemplate = template;
        origin = new Location(template.getWorld(), 0, 100, 0);
    }

    @Override
    public void init() {
        pulseSingularitySFX = new PulseSingularitySFX(origin);
        impulsionSingularitySFX = new ImpulsionSingularitySFX(origin);
        instabilitySingularitySFX = new InstabilitySingularitySFX(origin);

        pulseSingularitySFX.start();
        impulsionSingularitySFX.start();
        instabilitySingularitySFX.start();
    }

    @Override
    public void save() {
        pulseSingularitySFX.stop();
        impulsionSingularitySFX.stop();
        instabilitySingularitySFX.stop();
    }
}
