package fr.openmc.core.bootstrap.features;

import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.integration.OMCLogger;

@FunctionalInterface
public interface FeatureFactory {
    Feature create() throws NoClassDefFoundError;

    default Feature create(FeatureLoadingType type) {
        Feature feature = null;
        String featureName = "null";
        try {
            feature = create();

            if ((type.equals(FeatureLoadingType.RUNTIME) && !(feature instanceof LoadAfterItemsAdder))
                || (type.equals(FeatureLoadingType.AFTER_IA) && feature instanceof LoadAfterItemsAdder)) {
                return feature;
            }
        } catch (NoClassDefFoundError e) {
            if (feature != null)
                featureName = feature.getClass().getName();

            OMCLogger.errorFormatted("Plugin has failed to start feature {}  because {} does not exist.",
                    featureName, e.getMessage());
        }
        return feature;
    }
}
