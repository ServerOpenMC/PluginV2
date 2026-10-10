package fr.openmc.core.lifecycle.interfaces;

import fr.openmc.core.registry.features.Feature;

/**
 * Interface permettant aux classes d'enregistrer une feature
 */
public interface HasFeature {
    /**
     * Feature à initialiser
     */
    Feature feature();
}
