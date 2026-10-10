package fr.openmc.core.lifecycle.registries;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;

import java.io.IOException;

@SuppressWarnings("UnstableApiUsage")
public interface LifecycleRegistry {
    default void bootstrap(BootstrapContext context) throws IOException {}
    default void init() {}
    default void postInit() {}

    default void stop() {}
}
