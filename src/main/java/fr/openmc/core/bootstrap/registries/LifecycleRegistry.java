package fr.openmc.core.bootstrap.registries;

public interface LifecycleRegistry {
    default void bootstrap() {}

    default void init() {}

    default void postInit() {}
}
