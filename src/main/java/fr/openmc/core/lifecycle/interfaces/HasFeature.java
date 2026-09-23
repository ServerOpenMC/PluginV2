package fr.openmc.core.lifecycle.interfaces;

import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.loading.FeatureEntry;

/**
 * Interface permettant aux classes d'enregistrer une feature
 */
public interface HasFeature {
    /**
     * Feature à initialiser
     */
    FeatureEntry<? extends Feature> feature();
}
