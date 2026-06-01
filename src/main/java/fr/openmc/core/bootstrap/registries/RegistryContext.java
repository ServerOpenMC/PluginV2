package fr.openmc.core.bootstrap.registries;

import java.util.function.Supplier;

public record RegistryContext(Supplier<LifecycleRegistry> registry, RegistryLoadingType... loadingTypes) {
}
