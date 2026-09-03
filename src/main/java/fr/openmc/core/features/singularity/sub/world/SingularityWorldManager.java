package fr.openmc.core.features.singularity.sub.world;

import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.singularity.sub.world.gravity.GravityListener;
import fr.openmc.core.features.singularity.sub.world.sfx.ImpulsionSingularitySFX;
import fr.openmc.core.features.singularity.sub.world.sfx.InstabilitySingularitySFX;
import fr.openmc.core.features.singularity.sub.world.sfx.PulseSingularitySFX;
import fr.openmc.core.registry.worldtemplates.WorldTemplate;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Set;

/**
 * Classe gérant les choses lié à la Dimension inclus dedans :
 * - les Impulsions de la Singularité
 * - la gravité des joueurs
 * - les intéractions avec la Singularité
 * - gravité spéciale
 */
public class SingularityWorldManager extends Feature implements HasListeners {

    @Getter
    private static Location origin;
    @Getter
    private static WorldTemplate worldTemplate;

    public static final String SINGULARITY_WORLD_NAME = "world_omc_singularity_singularity_world";

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

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                GravityListener::new
        );
    }
}
