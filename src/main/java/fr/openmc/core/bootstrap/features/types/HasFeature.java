package fr.openmc.core.bootstrap.features.types;

import fr.openmc.core.bootstrap.features.Feature;

/**
 * Interface permettant aux classes d'enregistrer une sous feature
 */
public interface HasFeature {
    /**
     * Feature à initialiser
     */
    Feature getFeature();
}
