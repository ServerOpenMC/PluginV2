package fr.openmc.core.bootstrap.registries;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;

@SuppressWarnings("UnstableApiUsage")
public interface LifecycleRegistry {
    default void bootstrap(BootstrapContext context) {}

    default void init() {}

    default void postInit() {}
}
