package fr.openmc.core.registry.ambient;

import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.injectors.BiomesInjector;

public interface BiomeAmbient {
    BiomeBuilder getBiomeBuilder();

    default BiomesInjector toBiomeInjector(String namespace, String id) {
        return new BiomesInjector(namespace).add(id, getBiomeBuilder());
    }
}
