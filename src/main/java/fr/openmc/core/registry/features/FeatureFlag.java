package fr.openmc.core.registry.features;

import java.util.function.Supplier;

public interface FeatureFlag {

    record NotInUnitTest() implements FeatureFlag {}

    record NeedApi(Supplier<Boolean> isEnabled, String label) implements FeatureFlag {}

}
