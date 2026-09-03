package fr.openmc.core.registry.poi;

import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.singularity.contents.poi.SingularityPoi;
import fr.openmc.core.features.singularity.contents.worldtemplates.SingularityWorldTemplate;
import fr.openmc.core.hooks.WorldGuardHook;
import fr.openmc.core.registry.poi.listeners.PoiDetectionListener;

import java.util.Set;

/**
 * Registre qui mets à la disposition d'enregistrer des POI (Point of Interest)
 * qui est conretement une region, entre 2 points
 */
public class CustomPoiRegistry extends Registry<String, CustomPoi>
        implements KeyedRegistry<String, CustomPoi>, HasListeners {

    // ** REGISTER POIs **
    public final CustomPoi SINGULARITY = register(new SingularityPoi());

    @Override
    public void postInit() {
        for (CustomPoi poi : values()) {
            poi.firstLoad();

            WorldGuardHook.registerWorldGuardRegion(poi.getKey(), poi.getWorld(), poi.getPos1(), poi.getPos2());
        }
    }

    @Override
    public String key(CustomPoi registryObject) {
        return registryObject.getKey().asString();
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                PoiDetectionListener::new
        );
    }
}